/*
Author:       Mike Adair madairATdmsolutions.ca
              Richard Greenwood rich@greenwoodmap.com
License:      LGPL as per: http://www.gnu.org/copyleft/lesser.html
              Note: This program is an almost direct port of the C library Proj4.
$Id: Proj.js 2956 2007-07-09 12:17:52Z steven $
*/

/**
 * Provides methods for coordinate transformations between map projections and 
 * longitude/latitude, including datum transformations.
 * 
 * Initialization of Proj objects is with a projection code, usually EPSG codes.
 * The code passed in will be stripped of colons (':') and converted to uppercase
 * for internal use.
 * If you know what map projections your application will be dealing with, the
 * definition for the projections can be included with the script tag when the 
 * application is being coded.  Otherwise, practically any projection definition
 * can be loaded dynamically at run-time with an AJAX request to a lookup service
 * such as spatialreference.org.
 * The actual code supporting the forward and inverse tansformations for each
 * projection class is loaded dynamically at run-time.  These may also be 
 * specified when the application is coded if the projections to be used are known
 * beforehand.
 * A projection object has properties for units and title strings.
 * All coordinates are handled as points which is a 2 element array where x is
 * the first element and y is the second.
 * For the transform() method pass in mapXY and a destination projection object
 * and it returns a map XY coordinate in the other projection
 */

/**
 * Global namespace object for Proj4js library to use
 */
Proj4js = {

    /**
     * Property: defaultDatum
     * The datum to use when no others a specified
     */
    defaultDatum: 'WGS84',                  //default datum

    /**
     * Property: proxyScript
     * A proxy script to execute AJAX requests in other domains. 
     */
    proxyScript: null,  //TBD: customize this for spatialreference.org output

    /**
     * Property: defsLookupService
     * AJAX service to retreive projection definition parameters from
     */
    defsLookupService: 'http://spatialreference.org/ref',

    /**
     * Property: libPath
     * internal: http server path to library code.
     * TBD figure this out automatically
     */
    libPath: '../lib/',

    /** 
    * Method: transform(source, dest, point)
    * Transform a point coordinate from one map projection to another.
    *
    * Parameters:
    * source - {Proj4js.Proj} source map projection for the transformation
    * dest - {Proj4js.Proj} destination map projection for the transformation
    * point - {Object} point to transform, may be geodetic (long, lat) or
    *     projected Cartesian (x,y), but should always have x,y properties.
    */
    transform : function(source, dest, point) {
        if (!source.readyToUse || !dest.readyToUse) {
            this.reportError("Proj4js initialization for "+source.srsCode+" not yet complete");
            return;
        }
        
        if (point.transformed) {
          this.log("point already transformed");
          return;
        }
        
        // Workaround for Spherical Mercator
        if ((source.srsProjNumber =="900913" && dest.datumCode != "WGS84") ||
            (dest.srsProjNumber == "900913" && source.datumCode != "WGS84")) {
            var wgs84 = Proj4js.WGS84;
            this.transform(source, wgs84, point);
            point.transformed = false;
            source = wgs84;
        }

        // Transform source points to long/lat, if they aren't already.
        if ( source.projName=="longlat") {
            point.x *= Proj4js.common.D2R;  // convert degrees to radians
            point.y *= Proj4js.common.D2R;
        } else {
            if (source.to_meter) {
                point.x *= source.to_meter;
                point.y *= source.to_meter;
            }
            source.inverse(point); // Convert Cartesian to longlat
        }

        // Adjust for the prime meridian if necessary
        if (source.from_greenwich) { 
            point.x += source.from_greenwich; 
        }

        // Convert datums if needed, and if possible.
        point = this.datum_transform( source.datum, dest.datum, point );

        // Adjust for the prime meridian if necessary
        if (dest.from_greenwich) { 
            point.x -= dest.from_greenwich; 
        }

        if( dest.projName=="longlat" ) {             
            // convert radians to decimal degrees
            point.x *= Proj4js.common.R2D;
            point.y *= Proj4js.common.R2D;
        } else  {               // else project
            dest.forward(point);
            if (dest.to_meter) {
                point.x /= dest.to_meter;
                point.y /= dest.to_meter;
            }
        }
        point.transformed = true;
        return point;
    }, // transform()

    /** datum_transform()
      source coordinate system definition,
      destination coordinate system definition,
      point to transform in geodetic coordinates (long, lat, height)
    */
    datum_transform : function( source, dest, point ) {

      // Short cut if the datums are identical.
      if( source.compare_datums( dest ) ) {
          return point; // in this case, zero is sucess,
                    // whereas cs_compare_datums returns 1 to indicate TRUE
                    // confusing, should fix this
      }
      
      // Explicitly skip datum transform by setting 'datum=none' as parameter for either source or dest
      if( source.datum_type == Proj4js.common.PJD_NODATUM  
          || dest.datum_type == Proj4js.common.PJD_NODATUM) {
          return point; 
      }
      
        // If this datum requires grid shifts, then apply it to geodetic coordinates.
        if( source.datum_type == Proj4js.common.PJD_GRIDSHIFT )
        {
          alert("ERROR: Grid shift transformations are not implemented yet.");
          /*
            pj_apply_gridshift( pj_param(source.params,"snadgrids").s, 0,
                                point_count, point_offset, x, y, z );
            CHECK_RETURN;

            src_a = SRS_WGS84_SEMIMAJOR;
            src_es = 0.006694379990;
          */
        }

        if( dest.datum_type == Proj4js.common.PJD_GRIDSHIFT )
        {
          alert("ERROR: Grid shift transformations are not implemented yet.");
          /*
            dst_a = ;
            dst_es = 0.006694379990;
          */
        }

        // Do we need to go through geocentric coordinates?
        if( source.es != dest.es || source.a != dest.a 
            || source.datum_type == Proj4js.common.PJD_3PARAM 
            || source.datum_type == Proj4js.common.PJD_7PARAM
            || dest.datum_type == Proj4js.common.PJD_3PARAM
            || dest.datum_type == Proj4js.common.PJD_7PARAM)
        {

          // Convert to geocentric coordinates.
          source.geodetic_to_geocentric( point );
          // CHECK_RETURN;

          // Convert between datums
          if( source.datum_type == Proj4js.common.PJD_3PARAM || source.datum_type == Proj4js.common.PJD_7PARAM ) {
            source.geocentric_to_wgs84(point);
            // CHECK_RETURN;
          }

          if( dest.datum_type == Proj4js.common.PJD_3PARAM || dest.datum_type == Proj4js.common.PJD_7PARAM ) {
            dest.geocentric_from_wgs84(point);
            // CHECK_RETURN;
          }

          // Convert back to geodetic coordinates
          dest.geocentric_to_geodetic( point );
            // CHECK_RETURN;
        }

      // Apply grid shift to destination if required
      if( dest.datum_type == Proj4js.common.PJD_GRIDSHIFT )
      {
        alert("ERROR: Grid shift transformations are not implemented yet.");
        // pj_apply_gridshift( pj_param(dest.params,"snadgrids").s, 1, point);
        // CHECK_RETURN;
      }
      return point;
    }, // cs_datum_transform

    /**
     * Function: reportError
     * An internal method to report errors back to user. Should be overridden
     * by applications to deliver error messages.
     */
    reportError: function(msg) {
    },

    /**
     * Function: log
     * An internal method to log events. 
     */
    log: function(msg) {
    },

    loadProjDefinition : function(proj) {

      //check in memory
      if (this.defs[proj.srsCode]) return this.defs[proj.srsCode];

      //set AJAX options
      var options = {
        method: 'get',
        asynchronous: false,          //need to wait until defs are loaded before proceeding
        onSuccess: this.defsLoadedFromDisk.bind(this,proj.srsCode)
      }
      
      //else check for def on the server
      var url = this.libPath + 'defs/' + proj.srsAuth.toUpperCase() + proj.srsProjNumber + '.js';
      new OpenLayers.Ajax.Request(url, options);
      if ( this.defs[proj.srsCode] ) return this.defs[proj.srsCode];

      //else load from web service via AJAX request
      if (this.proxyScript) {
        var url = this.proxyScript + this.defsLookupService +'/' + proj.srsAuth +'/'+ proj.srsProjNumber + '/proj4';
        options.onSuccess = this.defsLoadedFromService.bind(this,proj.srsCode)
        options.onFailure = this.defsFailed.bind(this,proj.srsCode);
        new OpenLayers.Ajax.Request(url, options);
      }
      
      //may return null here if the defs are not found
      return this.defs[proj.srsCode];
    },

    defsLoadedFromDisk: function(srsCode, transport) {
      eval(transport.responseText);
    },

    defsLoadedFromService: function(srsCode, transport) {
      this.defs[srsCode] = transport.responseText;
      // save this also in the prototype, so we don't need to fetch it again
      Proj4js.defs[srsCode] = transport.responseText;
    },

    defsFailed: function(srsCode) {
      this.reportError('failed to load projection definition for: '+srsCode);
      OpenLayers.Util.extend(this.defs[srsCode], this.defs['WGS84']);  //set it to something so it can at least continue
    },

    loadProjCode : function(projName) {
      if (this.Proj[projName]) return;

      //set AJAX options
      var options = {
        method: 'get',
        asynchronous: false,          //need to wait until defs are loaded before proceeding
        onSuccess: this.loadProjCodeSuccess.bind(this, projName),
        onFailure: this.loadProjCodeFailure.bind(this, projName)
      };
      
      //load the projection class 
      var url = this.libPath + 'projCode/' + projName + '.js';
      new OpenLayers.Ajax.Request(url, options);
    },

    loadProjCodeSuccess : function(projName, transport) {
      eval(transport.responseText);
      if (this.Proj[projName].dependsOn){
        this.loadProjCode(this.Proj[projName].dependsOn);
      }
    },

    loadProjCodeFailure : function(projName) {
      Proj4js.reportError("failed to find projection file for: " + projName);
      //TBD initialize with identity transforms so proj will still work
    }

};

/**
 * Class: Proj4js.Proj
 * Projection objects provide coordinate transformation methods for point coordinates
 * once they have been initialized with a projection code.
 */
Proj4js.Proj = OpenLayers.Class({

  /**
   * Property: readyToUse
   * Flag to indicate if initialization is complete for this Proj object
   */
  readyToUse : false,   
  
  /**
   * Property: title
   * The title to describe the projection
   */
  title: null,  
  
  /**
   * Property: projName
   * The projection class for this projection, e.g. lcc (lambert conformal conic,
   * or merc for mercator.  These are exactly equicvalent to their Proj4 
   * counterparts.
   */
  projName: null,
  /**
   * Property: units
   * The units of the projection.  Values include 'm' and 'degrees'
   */
  units: null,
  /**
   * Property: datum
   * The datum specified for the projection
   */
  datum: null,

  /**
   * Constructor: initialize
   * Constructor for Proj4js.Proj objects
  *
  * Parameters:
  * srsCode - a code for map projection definition parameters.  These are usually
  * (but not always) EPSG codes.
  */
  initialize : function(srsCode) {
      this.srsCode = srsCode.toUpperCase();
      if (this.srsCode.indexOf("EPSG") == 0) {
          this.srsCode = this.srsCode;
          this.srsAuth = 'epsg';
          this.srsProjNumber = this.srsCode.substring(5);
      } else {
          this.srsAuth = '';
          this.srsProjNumber = this.srsCode;
      }

      var defs = Proj4js.loadProjDefinition(this);
      if (defs) {
          this.parseDefs(defs);
          Proj4js.loadProjCode(this.projName);
          this.callInit();
      }

  },

  callInit : function() {
      Proj4js.log('projection script loaded for:' + this.projName);
      OpenLayers.Util.extend(this, Proj4js.Proj[this.projName]);
      this.init();
      this.mapXYToLonLat = this.inverse;
      this.lonLatToMapXY = this.forward;
      this.readyToUse = true;
  },

  parseDefs : function(proj4opts) {
      this.defData = proj4opts;
      var paramName, paramVal;
      var paramArray=this.defData.split("+");

      for (var prop=0; prop<paramArray.length; prop++) {
          var property = paramArray[prop].split("=");
          paramName = property[0].toLowerCase();
          paramVal = property[1];

          switch (paramName.replace(/\s/gi,"")) {  // trim out spaces
              case "": break;   // throw away nameless parameter
              case "title":  this.title = paramVal; break;
              case "proj":   this.projName =  paramVal.replace(/\s/gi,""); break;
              case "units":  this.units = paramVal.replace(/\s/gi,""); break;
              case "datum":  this.datumCode = paramVal.replace(/\s/gi,""); break;
              case "nadgrids": this.nagrids = paramVal.replace(/\s/gi,""); break;
              case "ellps":  this.ellps = paramVal.replace(/\s/gi,""); break;
              case "a":      this.a =  parseFloat(paramVal); break;  // semi-major radius
              case "b":      this.b =  parseFloat(paramVal); break;  // semi-minor radius
              case "lat_0":  this.lat0 = paramVal*Proj4js.common.D2R; break;        // phi0, central latitude
              case "lat_1":  this.lat1 = paramVal*Proj4js.common.D2R; break;        //standard parallel 1
              case "lat_2":  this.lat2 = paramVal*Proj4js.common.D2R; break;        //standard parallel 2
              case "lat_ts": this.lat_ts = paramVal*Proj4js.common.D2R; break;      //used in merc 
              case "lon_0":  this.long0 = paramVal*Proj4js.common.D2R; break;       // lam0, central longitude
              case "x_0":    this.x0 = parseFloat(paramVal); break;  // false easting
              case "y_0":    this.y0 = parseFloat(paramVal); break;  // false northing
              case "k_0":    this.k0 = parseFloat(paramVal); break;  // projection scale factor
              case "k":      this.k0 = parseFloat(paramVal); break;  // both forms returned
              case "R_A":    this.R = parseFloat(paramVal); break;   //Spheroid radius 
              case "zone":   this.zone = parseInt(paramVal); break;  // UTM Zone
              case "south":   this.utmSouth = true; break;  // UTM north/south
              case "towgs84":this.datum_params = paramVal.split(","); break;
              case "to_meter": this.to_meter = parseFloat(paramVal); break; // cartesian scaling
              case "from_greenwich": this.from_greenwich = paramVal*Proj4js.common.D2R; break;
              case "pm":     paramVal = paramVal.replace(/\s/gi,"");
                             this.from_greenwich = Proj4js.PrimeMeridian[paramVal] ?
                                Proj4js.PrimeMeridian[paramVal]*Proj4js.common.D2R : 0.0; break;
              case "no_defs": break; 
              default: Proj4js.log("Unrecognized parameter: " + paramName);
          } // switch()
      } // for paramArray
      this.deriveConstants();
  },

  deriveConstants : function() {
      if (this.nagrids == '@null') this.datumCode = 'none';
      if (this.datumCode && this.datumCode != 'none') {
        var datumDef = Proj4js.Datum[this.datumCode];
        if (datumDef) {
          this.datum_params = datumDef.towgs84.split(',');
          this.ellps = datumDef.ellipse;
          this.datumName = datumDef.datumName;
        }
      }
      if (!this.a) {    // do we have an ellipsoid?
          var ellipse = Proj4js.Ellipsoid[this.ellps] ? Proj4js.Ellipsoid[this.ellps] : Proj4js.Ellipsoid['WGS84'];
          OpenLayers.Util.extend(this, ellipse);
      }
      if (this.rf && !this.b) this.b = (1.0 - 1.0/this.rf) * this.a;
      if (Math.abs(this.a - this.b)<Proj4js.common.EPSLN) this.sphere = true;
      this.a2 = this.a * this.a;          // used in geocentric
      this.b2 = this.b * this.b;          // used in geocentric
      this.es = (this.a2-this.b2)/this.a2;  // e ^ 2
      //this.es=1-(Math.pow(this.b,2)/Math.pow(this.a,2));
      this.e = Math.sqrt(this.es);        // eccentricity
      this.ep2=(this.a2-this.b2)/this.b2; // used in geocentric
      if (!this.k0) this.k0 = 1.0;    //default value

      this.datum = new Proj4js.datum(this);
  }
});

Proj4js.Proj.longlat = {
  init : function() {
    //no-op for longlat
  },
  forward : function(pt) {
    //identity transform
    return pt;
  },
  inverse : function(pt) {
    //identity transform
    return pt;
  }
};

/**
  Proj4js.defs is a collection of coordinate system definition objects in the 
  Proj4 command line format.
  Generally a def is added by means of a separate .js file for example:

    <SCRIPT type="text/javascript" src="defs/EPSG26912.js"></SCRIPT>

  def is a CS definition in PROJ.4 WKT format, for example:
    +proj="tmerc"   //longlat, etc.
    +a=majorRadius
    +b=minorRadius
    +lat0=somenumber
    +long=somenumber
*/
Proj4js.defs = {
  // These are so widely used, we'll go ahead and throw them in
  // without requiring a separate .js file
  'WGS84': "+title=long/lat:WGS84 +proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees",
  'EPSG:4326': "+title=long/lat:WGS84 +proj=longlat +a=6378137.0 +b=6356752.31424518 +ellps=WGS84 +datum=WGS84 +units=degrees",
  'EPSG:4269': "+title=long/lat:NAD83 +proj=longlat +a=6378137.0 +b=6356752.31414036 +ellps=GRS80 +datum=NAD83 +units=degrees" 
};
//+a=6378137.0 +b=6356752.31424518 +ellps=WGS84 +datum=WGS84",
Proj4js.common = {
  PI : Math.PI,
  HALF_PI : Math.PI*0.5,
  TWO_PI : Math.PI*2,
  FORTPI : 0.78539816339744833,
  R2D : 57.2957795131,
  D2R : 0.0174532925199,
  SEC_TO_RAD : 4.84813681109535993589914102357e-6, /* SEC_TO_RAD = Pi/180/3600 */
  EPSLN : 1.0e-10,
  MAX_ITER : 20,
  // following constants from geocent.c
  COS_67P5 : 0.38268343236508977,  /* cosine of 67.5 degrees */
  AD_C : 1.0026000,                /* Toms region 1 constant */

  /* datum_type values */
  PJD_UNKNOWN  : 0,
  PJD_3PARAM   : 1,
  PJD_7PARAM   : 2,
  PJD_GRIDSHIFT: 3,
  PJD_WGS84    : 4,   // WGS84 or equivalent
  PJD_NODATUM  : 5,   // WGS84 or equivalent
  SRS_WGS84_SEMIMAJOR : 6378137.0,  // only used in grid shift transforms

// Function to compute the constant small m which is the radius of
//   a parallel of latitude, phi, divided by the semimajor axis.
// -----------------------------------------------------------------
  msfnz : function(eccent, sinphi, cosphi) {
      var con = eccent * sinphi;
      return cosphi/(Math.sqrt(1.0 - con * con));
  },

// Function to compute the constant small t for use in the forward
//   computations in the Lambert Conformal Conic and the Polar
//   Stereographic projections.
// -----------------------------------------------------------------
  tsfnz : function(eccent, phi, sinphi) {
    var con = eccent * sinphi;
    var com = .5 * eccent;
    con = Math.pow(((1.0 - con) / (1.0 + con)), com);
    return (Math.tan(.5 * (this.HALF_PI - phi))/con);
  },

// Function to compute the latitude angle, phi2, for the inverse of the
//   Lambert Conformal Conic and Polar Stereographic projections.
// ----------------------------------------------------------------
  phi2z : function(eccent, ts) {
    var eccnth = .5 * eccent;
    var con, dphi;
    var phi = this.HALF_PI - 2 * Math.atan(ts);
    for (i = 0; i <= 15; i++) {
      con = eccent * Math.sin(phi);
      dphi = this.HALF_PI - 2 * Math.atan(ts *(Math.pow(((1.0 - con)/(1.0 + con)),eccnth))) - phi;
      phi += dphi;
      if (Math.abs(dphi) <= .0000000001) return phi;
    }
    alert("phi2z has NoConvergence");
    return (-9999);
  },

/* Function to compute constant small q which is the radius of a 
   parallel of latitude, phi, divided by the semimajor axis. 
------------------------------------------------------------*/
  qsfnz : function(eccent,sinphi,cosphi) {
    var con;
    if (eccent > 1.0e-7) {
      con = eccent * sinphi;
      return (( 1.0- eccent * eccent) * (sinphi /(1.0 - con * con) - (.5/eccent)*Math.log((1.0 - con)/(1.0 + con))));
    } else {
      return(2.0 * sinphi);
    }
  },

/* Function to eliminate roundoff errors in asin
----------------------------------------------*/
  asinz : function(x) {
    if (Math.abs(x)>1.0) {
      x=(x>1.0)?1.0:-1.0;
    }
    return Math.asin(x);
  },

// following functions from gctpc cproj.c for transverse mercator projections
  e0fn : function(x) {return(1.0-0.25*x*(1.0+x/16.0*(3.0+1.25*x)));},
  e1fn : function(x) {return(0.375*x*(1.0+0.25*x*(1.0+0.46875*x)));},
  e2fn : function(x) {return(0.05859375*x*x*(1.0+0.75*x));},
  e3fn : function(x) {return(x*x*x*(35.0/3072.0));},
  mlfn : function(e0,e1,e2,e3,phi) {return(e0*phi-e1*Math.sin(2.0*phi)+e2*Math.sin(4.0*phi)-e3*Math.sin(6.0*phi));},

  srat : function(esinp, exp) {
    return(Math.pow((1.0-esinp)/(1.0+esinp), exp));
  },

// Function to return the sign of an argument
  sign : function(x) { if (x < 0.0) return(-1); else return(1);},

// Function to adjust longitude to -180 to 180; input in radians
  adjust_lon : function(x) {
    x = (Math.abs(x) < this.PI) ? x: (x - (this.sign(x)*this.TWO_PI) );
    return x;
  }

};

/** datum object
*/
Proj4js.datum = OpenLayers.Class({

  initialize : function(proj) {
    this.datum_type = Proj4js.common.PJD_WGS84;   //default setting
    if (proj.datumCode && proj.datumCode == 'none') {
      this.datum_type = Proj4js.common.PJD_NODATUM;
    }
    if (proj && proj.datum_params) {
      for (var i=0; i<proj.datum_params.length; i++) {
        proj.datum_params[i]=parseFloat(proj.datum_params[i]);
      }
      if (proj.datum_params[0] != 0 || proj.datum_params[1] != 0 || proj.datum_params[2] != 0 ) {
        this.datum_type = Proj4js.common.PJD_3PARAM;
      }
      if (proj.datum_params.length > 3) {
        if (proj.datum_params[3] != 0 || proj.datum_params[4] != 0 ||
            proj.datum_params[5] != 0 || proj.datum_params[6] != 0 ) {
          this.datum_type = Proj4js.common.PJD_7PARAM;
          proj.datum_params[3] *= Proj4js.common.SEC_TO_RAD;
          proj.datum_params[4] *= Proj4js.common.SEC_TO_RAD;
          proj.datum_params[5] *= Proj4js.common.SEC_TO_RAD;
          proj.datum_params[6] = (proj.datum_params[6]/1000000.0) + 1.0;
        }
      }
    }
    if (proj) {
      this.a = proj.a;    //datum object also uses these values
      this.b = proj.b;
      this.es = proj.es;
      this.ep2 = proj.ep2;
      this.datum_params = proj.datum_params;
    }
  },

  /****************************************************************/
  // cs_compare_datums()
  //   Returns 1 (TRUE) if the two datums match, otherwise 0 (FALSE).
  compare_datums : function( dest ) {
    if( this.datum_type != dest.datum_type ) {
      return false; // false, datums are not equal
    } else if (this.a != dest.a || Math.abs(this.es-dest.es) > 0.000000000050) {
      // the tolerence for es is to ensure that GRS80 and WGS84
      // are considered identical
      return false;
    } else if( this.datum_type == Proj4js.common.PJD_3PARAM ) {
      return (this.datum_params[0] == dest.datum_params[0]
              && this.datum_params[1] == dest.datum_params[1]
              && this.datum_params[2] == dest.datum_params[2]);
    } else if( this.datum_type == Proj4js.common.PJD_7PARAM ) {
      return (this.datum_params[0] == dest.datum_params[0]
              && this.datum_params[1] == dest.datum_params[1]
              && this.datum_params[2] == dest.datum_params[2]
              && this.datum_params[3] == dest.datum_params[3]
              && this.datum_params[4] == dest.datum_params[4]
              && this.datum_params[5] == dest.datum_params[5]
              && this.datum_params[6] == dest.datum_params[6]);
    } else if( this.datum_type == Proj4js.common.PJD_GRIDSHIFT ) {
      return strcmp( pj_param(this.params,"snadgrids").s,
                     pj_param(dest.params,"snadgrids").s ) == 0;
    } else {
      return true; // datums are equal
    }
  }, // cs_compare_datums()

  /*
   * The function Convert_Geodetic_To_Geocentric converts geodetic coordinates
   * (latitude, longitude, and height) to geocentric coordinates (X, Y, Z),
   * according to the current ellipsoid parameters.
   *
   *    Latitude  : Geodetic latitude in radians                     (input)
   *    Longitude : Geodetic longitude in radians                    (input)
   *    Height    : Geodetic height, in meters                       (input)
   *    X         : Calculated Geocentric X coordinate, in meters    (output)
   *    Y         : Calculated Geocentric Y coordinate, in meters    (output)
   *    Z         : Calculated Geocentric Z coordinate, in meters    (output)
   *
   */
  geodetic_to_geocentric : function(p) {
    var Longitude = p.x;
    var Latitude = p.y;
    var Height = p.z ? p.z : 0;   //Z value not always supplied
    var X;  // output
    var Y;
    var Z;

    var Error_Code=0;  //  GEOCENT_NO_ERROR;
    var Rn;            /*  Earth radius at location  */
    var Sin_Lat;       /*  Math.sin(Latitude)  */
    var Sin2_Lat;      /*  Square of Math.sin(Latitude)  */
    var Cos_Lat;       /*  Math.cos(Latitude)  */

    /*
    ** Don't blow up if Latitude is just a little out of the value
    ** range as it may just be a rounding issue.  Also removed longitude
    ** test, it should be wrapped by Math.cos() and Math.sin().  NFW for PROJ.4, Sep/2001.
    */
    if (Latitude < -Proj4js.common.HALF_PI && Latitude > -1.001 * Proj4js.common.HALF_PI ) {
        Latitude = -Proj4js.common.HALF_PI;
    } else if (Latitude > Proj4js.common.HALF_PI && Latitude < 1.001 * Proj4js.common.HALF_PI ) {
        Latitude = Proj4js.common.HALF_PI;
    } else if ((Latitude < -Proj4js.common.HALF_PI) || (Latitude > Proj4js.common.HALF_PI)) {
      /* Latitude out of range */
      Proj4js.reportError('geocent:lat out of range:'+Latitude);
      return null;
    }

    if (Longitude > Proj4js.common.PI) Longitude -= (2*Proj4js.common.PI);
    Sin_Lat = Math.sin(Latitude);
    Cos_Lat = Math.cos(Latitude);
    Sin2_Lat = Sin_Lat * Sin_Lat;
    Rn = this.a / (Math.sqrt(1.0e0 - this.es * Sin2_Lat));
    X = (Rn + Height) * Cos_Lat * Math.cos(Longitude);
    Y = (Rn + Height) * Cos_Lat * Math.sin(Longitude);
    Z = ((Rn * (1 - this.es)) + Height) * Sin_Lat;

    p.x = X;
    p.y = Y;
    p.z = Z;
    return Error_Code;
  }, // cs_geodetic_to_geocentric()

  geocentric_to_geodetic : function (p) {
/* local defintions and variables */
/* end-criterium of loop, accuracy of sin(Latitude) */
var genau = 1.E-12;
var genau2 = (genau*genau);
var maxiter = 30;

    var P;        /* distance between semi-minor axis and location */
    var RR;       /* distance between center and location */
    var CT;       /* sin of geocentric latitude */
    var ST;       /* cos of geocentric latitude */
    var RX;
    var RK;
    var RN;       /* Earth radius at location */
    var CPHI0;    /* cos of start or old geodetic latitude in iterations */
    var SPHI0;    /* sin of start or old geodetic latitude in iterations */
    var CPHI;     /* cos of searched geodetic latitude */
    var SPHI;     /* sin of searched geodetic latitude */
    var SDPHI;    /* end-criterium: addition-theorem of sin(Latitude(iter)-Latitude(iter-1)) */
    var At_Pole;     /* indicates location is in polar region */
    var iter;        /* # of continous iteration, max. 30 is always enough (s.a.) */

    var X =p.x;
    var Y = p.y;
    var Z = p.z ? p.z : 0.0;   //Z value not always supplied
    var Longitude;
    var Latitude;
    var Height;
    
    At_Pole = false;
    P = Math.sqrt(X*X+Y*Y);
    RR = Math.sqrt(X*X+Y*Y+Z*Z);

/*	special cases for latitude and longitude */
    if (P/this.a < genau) {

/*  special case, if P=0. (X=0., Y=0.) */
        At_Pole = true;
        Longitude = 0.0;

/*  if (X,Y,Z)=(0.,0.,0.) then Height becomes semi-minor axis
 *  of ellipsoid (=center of mass), Latitude becomes PI/2 */
        if (RR/this.a < genau) {
            Latitude = Proj4js.common.HALF_PI;
            Height   = -this.b;
            return;
        }
    } else {
/*  ellipsoidal (geodetic) longitude
 *  interval: -PI < Longitude <= +PI */
        Longitude=Math.atan2(Y,X);
    }

/* --------------------------------------------------------------
 * Following iterative algorithm was developped by
 * "Institut für Erdmessung", University of Hannover, July 1988.
 * Internet: www.ife.uni-hannover.de
 * Iterative computation of CPHI,SPHI and Height.
 * Iteration of CPHI and SPHI to 10**-12 radian resp.
 * 2*10**-7 arcsec.
 * --------------------------------------------------------------
 */
    CT = Z/RR;
    ST = P/RR;
    RX = 1.0/Math.sqrt(1.0-this.es*(2.0-this.es)*ST*ST);
    CPHI0 = ST*(1.0-this.es)*RX;
    SPHI0 = CT*RX;
    iter = 0;

/* loop to find sin(Latitude) resp. Latitude
 * until |sin(Latitude(iter)-Latitude(iter-1))| < genau */
    do
    {
        iter++;
        RN = this.a/Math.sqrt(1.0-this.es*SPHI0*SPHI0);

/*  ellipsoidal (geodetic) height */
        Height = P*CPHI0+Z*SPHI0-RN*(1.0-this.es*SPHI0*SPHI0);

        RK = this.es*RN/(RN+Height);
        RX = 1.0/Math.sqrt(1.0-RK*(2.0-RK)*ST*ST);
        CPHI = ST*(1.0-RK)*RX;
        SPHI = CT*RX;
        SDPHI = SPHI*CPHI0-CPHI*SPHI0;
        CPHI0 = CPHI;
        SPHI0 = SPHI;
    }
    while (SDPHI*SDPHI > genau2 && iter < maxiter);

/*	ellipsoidal (geodetic) latitude */
    Latitude=Math.atan(SPHI/Math.abs(CPHI));

    p.x = Longitude;
    p.y =Latitude;
    p.z = Height;
    return p;
  },

  /** Convert_Geocentric_To_Geodetic
   * The method used here is derived from 'An Improved Algorithm for
   * Geocentric to Geodetic Coordinate Conversion', by Ralph Toms, Feb 1996
   */
  geocentric_to_geodetic_noniter : function (p) {
    var X =p.x;
    var Y = p.y;
    var Z = p.z ? p.z : 0;   //Z value not always supplied
    var Longitude;
    var Latitude;
    var Height;

    var W;        /* distance from Z axis */
    var W2;       /* square of distance from Z axis */
    var T0;       /* initial estimate of vertical component */
    var T1;       /* corrected estimate of vertical component */
    var S0;       /* initial estimate of horizontal component */
    var S1;       /* corrected estimate of horizontal component */
    var Sin_B0;   /* Math.sin(B0), B0 is estimate of Bowring aux variable */
    var Sin3_B0;  /* cube of Math.sin(B0) */
    var Cos_B0;   /* Math.cos(B0) */
    var Sin_p1;   /* Math.sin(phi1), phi1 is estimated latitude */
    var Cos_p1;   /* Math.cos(phi1) */
    var Rn;       /* Earth radius at location */
    var Sum;      /* numerator of Math.cos(phi1) */
    var At_Pole;  /* indicates location is in polar region */

    X = parseFloat(X);  // cast from string to float
    Y = parseFloat(Y);
    Z = parseFloat(Z);

    At_Pole = false;
    if (X != 0.0)
    {
        Longitude = Math.atan2(Y,X);
    }
    else
    {
        if (Y > 0)
        {
            Longitude = Proj4js.common.HALF_PI;
        }
        else if (Y < 0)
        {
            Longitude = -Proj4js.common.HALF_PI;
        }
        else
        {
            At_Pole = true;
            Longitude = 0.0;
            if (Z > 0.0)
            {  /* north pole */
                Latitude = Proj4js.common.HALF_PI;
            }
            else if (Z < 0.0)
            {  /* south pole */
                Latitude = -Proj4js.common.HALF_PI;
            }
            else
            {  /* center of earth */
                Latitude = Proj4js.common.HALF_PI;
                Height = -this.b;
                return;
            }
        }
    }
    W2 = X*X + Y*Y;
    W = Math.sqrt(W2);
    T0 = Z * Proj4js.common.AD_C;
    S0 = Math.sqrt(T0 * T0 + W2);
    Sin_B0 = T0 / S0;
    Cos_B0 = W / S0;
    Sin3_B0 = Sin_B0 * Sin_B0 * Sin_B0;
    T1 = Z + this.b * this.ep2 * Sin3_B0;
    Sum = W - this.a * this.es * Cos_B0 * Cos_B0 * Cos_B0;
    S1 = Math.sqrt(T1*T1 + Sum * Sum);
    Sin_p1 = T1 / S1;
    Cos_p1 = Sum / S1;
    Rn = this.a / Math.sqrt(1.0 - this.es * Sin_p1 * Sin_p1);
    if (Cos_p1 >= Proj4js.common.COS_67P5)
    {
        Height = W / Cos_p1 - Rn;
    }
    else if (Cos_p1 <= -Proj4js.common.COS_67P5)
    {
        Height = W / -Cos_p1 - Rn;
    }
    else
    {
        Height = Z / Sin_p1 + Rn * (this.es - 1.0);
    }
    if (At_Pole == false)
    {
        Latitude = Math.atan(Sin_p1 / Cos_p1);
    }

    p.x = Longitude;
    p.y =Latitude;
    p.z = Height;
    return p;
  }, // cs_geocentric_to_geodetic()

  /****************************************************************/
  // pj_geocentic_to_wgs84( p )
  //  p = point to transform in geocentric coordinates (x,y,z)
  geocentric_to_wgs84 : function ( p ) {

    if( this.datum_type == Proj4js.common.PJD_3PARAM )
    {
      // if( x[io] == HUGE_VAL )
      //    continue;
      p.x += this.datum_params[0];
      p.y += this.datum_params[1];
      p.z += this.datum_params[2];

    }
    else if (this.datum_type == Proj4js.common.PJD_7PARAM)
    {
      var Dx_BF =this.datum_params[0];
      var Dy_BF =this.datum_params[1];
      var Dz_BF =this.datum_params[2];
      var Rx_BF =this.datum_params[3];
      var Ry_BF =this.datum_params[4];
      var Rz_BF =this.datum_params[5];
      var M_BF  =this.datum_params[6];
      // if( x[io] == HUGE_VAL )
      //    continue;
      var x_out = M_BF*(       p.x - Rz_BF*p.y + Ry_BF*p.z) + Dx_BF;
      var y_out = M_BF*( Rz_BF*p.x +       p.y - Rx_BF*p.z) + Dy_BF;
      var z_out = M_BF*(-Ry_BF*p.x + Rx_BF*p.y +       p.z) + Dz_BF;
      p.x = x_out;
      p.y = y_out;
      p.z = z_out;
    }
  }, // cs_geocentric_to_wgs84

  /****************************************************************/
  // pj_geocentic_from_wgs84()
  //  coordinate system definition,
  //  point to transform in geocentric coordinates (x,y,z)
  geocentric_from_wgs84 : function( p ) {

    if( this.datum_type == Proj4js.common.PJD_3PARAM )
    {
      //if( x[io] == HUGE_VAL )
      //    continue;
      p.x -= this.datum_params[0];
      p.y -= this.datum_params[1];
      p.z -= this.datum_params[2];

    }
    else if (this.datum_type == Proj4js.common.PJD_7PARAM)
    {
      var Dx_BF =this.datum_params[0];
      var Dy_BF =this.datum_params[1];
      var Dz_BF =this.datum_params[2];
      var Rx_BF =this.datum_params[3];
      var Ry_BF =this.datum_params[4];
      var Rz_BF =this.datum_params[5];
      var M_BF  =this.datum_params[6];
      var x_tmp = (p.x - Dx_BF) / M_BF;
      var y_tmp = (p.y - Dy_BF) / M_BF;
      var z_tmp = (p.z - Dz_BF) / M_BF;
      //if( x[io] == HUGE_VAL )
      //    continue;

      p.x =        x_tmp + Rz_BF*y_tmp - Ry_BF*z_tmp;
      p.y = -Rz_BF*x_tmp +       y_tmp + Rx_BF*z_tmp;
      p.z =  Ry_BF*x_tmp - Rx_BF*y_tmp +       z_tmp;
    } //cs_geocentric_from_wgs84()
  }
});

/** point object, nothing fancy, just allows values to be
    passed back and forth by reference rather than by value.
    Other point classes may be used as long as they have
    x and y properties, which will get modified in the transform method.
*/
Proj4js.Point = OpenLayers.Class({

    initialize : function(x,y,z) {
      if (typeof x == 'object') {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2] || 0.0;
      } else {
        this.x = x;
        this.y = y;
        this.z = z || 0.0;
      }
    },

    clone : function() {
      return new Proj4js.Point(this.x, this.y, this.z);
    },

    /**
     * Method: toString
     * Return a readable string version of the lonlat
     *
     * Return:
     * {String} String representation of Proj4js.Point object. 
     *           (ex. <i>"x=5,y=42"</i>)
     */
    toString:function() {
        return ("x=" + this.x + ",y=" + this.y);
    },

    /** 
     * APIMethod: toShortString
     * 
     * Return:
     * {String} Shortened String representation of Proj4js.Point object. 
     *         (ex. <i>"5, 42"</i>)
     */
    toShortString:function() {
        return (this.x + ", " + this.y);
    }
});

Proj4js.PrimeMeridian = {
    "greenwich": 0.0,               //"0dE",
    "lisbon":     -9.131906111111,   //"9d07'54.862\"W",
    "paris":       2.337229166667,   //"2d20'14.025\"E",
    "bogota":    -74.080916666667,  //"74d04'51.3\"W",
    "madrid":     -3.687938888889,  //"3d41'16.58\"W",
    "rome":       12.452333333333,  //"12d27'8.4\"E",
    "bern":        7.439583333333,  //"7d26'22.5\"E",
    "jakarta":   106.807719444444,  //"106d48'27.79\"E",
    "ferro":     -17.666666666667,  //"17d40'W",
    "brussels":    4.367975,        //"4d22'4.71\"E",
    "stockholm":  18.058277777778,  //"18d3'29.8\"E",
    "athens":     23.7163375,       //"23d42'58.815\"E",
    "oslo":       10.722916666667   //"10d43'22.5\"E"
};

Proj4js.Ellipsoid = {
  "MERIT": {a:6378137.0, rf:298.257, ellipseName:"MERIT 1983"},
  "SGS85": {a:6378136.0, rf:298.257, ellipseName:"Soviet Geodetic System 85"},
  "GRS80": {a:6378137.0, rf:298.257222101, ellipseName:"GRS 1980(IUGG, 1980)"},
  "IAU76": {a:6378140.0, rf:298.257, ellipseName:"IAU 1976"},
  "airy": {a:6377563.396, b:6356256.910, ellipseName:"Airy 1830"},
  "APL4.": {a:6378137, rf:298.25, ellipseName:"Appl. Physics. 1965"},
  "NWL9D": {a:6378145.0, rf:298.25, ellipseName:"Naval Weapons Lab., 1965"},
  "mod_airy": {a:6377340.189, b:6356034.446, ellipseName:"Modified Airy"},
  "andrae": {a:6377104.43, rf:300.0, ellipseName:"Andrae 1876 (Den., Iclnd.)"},
  "aust_SA": {a:6378160.0, rf:298.25, ellipseName:"Australian Natl & S. Amer. 1969"},
  "GRS67": {a:6378160.0, rf:298.2471674270, ellipseName:"GRS 67(IUGG 1967)"},
  "bessel": {a:6377397.155, rf:299.1528128, ellipseName:"Bessel 1841"},
  "bess_nam": {a:6377483.865, rf:299.1528128, ellipseName:"Bessel 1841 (Namibia)"},
  "clrk66": {a:6378206.4, b:6356583.8, ellipseName:"Clarke 1866"},
  "clrk80": {a:6378249.145, rf:293.4663, ellipseName:"Clarke 1880 mod."},
  "CPM": {a:6375738.7, rf:334.29, ellipseName:"Comm. des Poids et Mesures 1799"},
  "delmbr": {a:6376428.0, rf:311.5, ellipseName:"Delambre 1810 (Belgium)"},
  "engelis": {a:6378136.05, rf:298.2566, ellipseName:"Engelis 1985"},
  "evrst30": {a:6377276.345, rf:300.8017, ellipseName:"Everest 1830"},
  "evrst48": {a:6377304.063, rf:300.8017, ellipseName:"Everest 1948"},
  "evrst56": {a:6377301.243, rf:300.8017, ellipseName:"Everest 1956"},
  "evrst69": {a:6377295.664, rf:300.8017, ellipseName:"Everest 1969"},
  "evrstSS": {a:6377298.556, rf:300.8017, ellipseName:"Everest (Sabah & Sarawak)"},
  "fschr60": {a:6378166.0, rf:298.3, ellipseName:"Fischer (Mercury Datum) 1960"},
  "fschr60m": {a:6378155.0, rf:298.3, ellipseName:"Fischer 1960"},
  "fschr68": {a:6378150.0, rf:298.3, ellipseName:"Fischer 1968"},
  "helmert": {a:6378200.0, rf:298.3, ellipseName:"Helmert 1906"},
  "hough": {a:6378270.0, rf:297.0, ellipseName:"Hough"},
  "intl": {a:6378388.0, rf:297.0, ellipseName:"International 1909 (Hayford)"},
  "kaula": {a:6378163.0, rf:298.24, ellipseName:"Kaula 1961"},
  "lerch": {a:6378139.0, rf:298.257, ellipseName:"Lerch 1979"},
  "mprts": {a:6397300.0, rf:191.0, ellipseName:"Maupertius 1738"},
  "new_intl": {a:6378157.5, b:6356772.2, ellipseName:"New International 1967"},
  "plessis": {a:6376523.0, rf:6355863.0, ellipseName:"Plessis 1817 (France)"},
  "krass": {a:6378245.0, rf:298.3, ellipseName:"Krassovsky, 1942"},
  "SEasia": {a:6378155.0, b:6356773.3205, ellipseName:"Southeast Asia"},
  "walbeck": {a:6376896.0, b:6355834.8467, ellipseName:"Walbeck"},
  "WGS60": {a:6378165.0, rf:298.3, ellipseName:"WGS 60"},
  "WGS66": {a:6378145.0, rf:298.25, ellipseName:"WGS 66"},
  "WGS72": {a:6378135.0, rf:298.26, ellipseName:"WGS 72"},
  "WGS84": {a:6378137.0, rf:298.257223563, ellipseName:"WGS 84"},
  "sphere": {a:6370997.0, b:6370997.0, ellipseName:"Normal Sphere (r=6370997)"}
};

Proj4js.Datum = {
  "WGS84": {towgs84: "0,0,0", ellipse: "WGS84", datumName: ""},
  "GGRS87": {towgs84: "-199.87,74.79,246.62", ellipse: "GRS80", datumName: "Greek_Geodetic_Reference_System_1987"},
  "NAD83": {towgs84: "0,0,0", ellipse: "GRS80", datumName: "North_American_Datum_1983"},
  "NAD27": {nadgrids: "@conus,@alaska,@ntv2_0.gsb,@ntv1_can.dat", ellipse: "clrk66", datumName: "North_American_Datum_1927"},
  "potsdam": {towgs84: "606.0,23.0,413.0", ellipse: "bessel", datumName: "Potsdam Rauenberg 1950 DHDN"},
  "carthage": {towgs84: "-263.0,6.0,431.0", ellipse: "clark80", datumName: "Carthage 1934 Tunisia"},
  "hermannskogel": {towgs84: "653.0,-212.0,449.0", ellipse: "bessel", datumName: "Hermannskogel"},
  "ire65": {towgs84: "482.530,-130.596,564.557,-1.042,-0.214,-0.631,8.15", ellipse: "mod_airy", datumName: "Ireland 1965"},
  "nzgd49": {towgs84: "59.47,-5.04,187.44,0.47,-0.1,1.024,-4.5993", ellipse: "intl", datumName: "New Zealand Geodetic Datum 1949"},
  "OSGB36": {towgs84: "446.448,-125.157,542.060,0.1502,0.2470,0.8421,-20.4894", ellipse: "airy", datumName: "Airy 1830"}
};

Proj4js.WGS84 = new Proj4js.Proj('WGS84');
Proj4js.Datum['OSB36'] = Proj4js.Datum['OSGB36']; //as returned from spatialreference.org

/* Copyright 2002-2013 CS Systèmes d'Information
 * Licensed to CS Systèmes d'Information (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cs.si.stavor.station;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.FastMath;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.frames.TopocentricFrame;


/** Orekit tutorial for computing visibility circles.
 * @author Luc Maisonobe
 */
public class VisibilityCircle {

    public static List<LatLon> computeCircle(BodyShape earth, double latitude, double longitude, double altitude,
                                                     String name, double minElevation, double radius, int points)
        throws OrekitException {

        // define Earth shape, using WGS84 model
        /*BodyShape earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                                               Constants.WGS84_EARTH_FLATTENING,
                                               FramesFactory.getITRF(IERSConventions.IERS_2010, false));
*/
    	
        // define an array of ground stations
        TopocentricFrame station =
                new TopocentricFrame(earth, new GeodeticPoint(latitude*Math.PI/180, longitude*Math.PI/180, altitude), name);

        // compute the visibility circle
        List<LatLon> circle = new ArrayList<LatLon>();
        for (int i = 0; i < points; ++i) {
        	double twoPi = 2.0 * FastMath.PI;
            double azimuth = i * (twoPi / points) + (FastMath.PI/2);
            if(azimuth > twoPi)
            	azimuth = azimuth - twoPi;
            GeodeticPoint gp = station.computeLimitVisibilityPoint(radius, azimuth, minElevation*Math.PI/180);
            circle.add(new LatLon(gp.getLatitude()*180/Math.PI,gp.getLongitude()*180/Math.PI));
        }

        // return the computed points
        return circle;

    }

}

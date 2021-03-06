/*
 * Copyright 2014, Mysema Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gvnix.web.datatables.util.querydsl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.mysema.query.spatial.SpatialOps;
import com.mysema.query.types.Operator;
import com.mysema.query.types.OperatorImpl;

public class HibernateSpatialSupport {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(HibernateSpatialSupport.class);

    private static Map<Operator<?>, String> spatialOps = null;

    public static Map<Operator<?>, String> getSpatialOps(String prefix,
            boolean asFunction) {
        if (spatialOps != null) {
            return spatialOps;
        }
        Map<Operator<?>, String> ops = Maps.newHashMap();

        // Initialize spatialOps whatever exception found
        spatialOps = ops;
        try {
            // Check for 3.4.1 QueryDSL required constructor
            OperatorImpl.class.getConstructor(String.class, String.class);
        }
        catch (NoSuchMethodException ex) {
            LOGGER.warn("No Hibernate Spatial supported added: Check queryDSL library version");
            LOGGER.debug("Exception Initializing Hibernate Spatial support", ex);
            return ops;
        }

        ops.put(SpatialOps.DIMENSION, "dimension({0})");
        ops.put(SpatialOps.GEOMETRY_TYPE, "geometrytype({0}, {1})");
        ops.put(SpatialOps.SRID, "srid({0})");
        ops.put(SpatialOps.ENVELOPE, "envelope({0})");
        ops.put(SpatialOps.AS_TEXT, "astext({0})");
        ops.put(SpatialOps.AS_BINARY, "asbinary({0})");
        ops.put(SpatialOps.IS_EMPTY, "isempty({0})");
        ops.put(SpatialOps.IS_SIMPLE, "issimple({0})");
        ops.put(SpatialOps.BOUNDARY, "boundary({0})");

        ops.put(SpatialOps.EQUALS, "equals({0}, {1})");
        ops.put(SpatialOps.DISJOINT, "disjoint({0}, {1})");
        ops.put(SpatialOps.INTERSECTS, "intersects({0}, {1}) = true");
        ops.put(SpatialOps.TOUCHES, "touches({0}, {1})");
        ops.put(SpatialOps.CROSSES, "crosses({0}, {1})");
        ops.put(SpatialOps.WITHIN, "within({0}, {1})");
        ops.put(SpatialOps.CONTAINS, "contains({0}, {1})");
        ops.put(SpatialOps.OVERLAPS, "overlaps({0}, {1})");
        ops.put(SpatialOps.RELATE, "relate({0}, {1}, {2})");

        ops.put(SpatialOps.DISTANCE, "distance({0}, {1})");
        ops.put(SpatialOps.BUFFER, "buffer({0}, {1})");
        ops.put(SpatialOps.CONVEXHULL, "convexhull({0})");
        ops.put(SpatialOps.INTERSECTION, "intersection({0}, {1})");
        ops.put(SpatialOps.UNION, "geomunion({0}, {1})");
        ops.put(SpatialOps.DIFFERENCE, "difference({0}, {1})");
        ops.put(SpatialOps.SYMDIFFERENCE, "symdifference({0}, {1})");
        // dwithin({0}, {1}, 2)
        ops.put(SpatialOps.TRANSFORM, "transform({0}, {1})");
        // extent({0})

        return ops;
    }
}

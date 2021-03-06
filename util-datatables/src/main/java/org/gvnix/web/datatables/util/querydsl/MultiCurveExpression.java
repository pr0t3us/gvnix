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

import javax.annotation.Nullable;

import com.mysema.query.spatial.SpatialOps;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.BooleanOperation;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.NumberOperation;
import com.vividsolutions.jts.geom.GeometryCollection;

/**
 * A MultiCurve is a 1-dimensional GeometryCollection whose elements are Curves.
 * 
 * @author tiwe
 * 
 * @param <T>
 */
public abstract class MultiCurveExpression<T extends GeometryCollection>
        extends GeometryCollectionExpression<T> {

    private static final long serialVersionUID = 6983316799469849656L;

    @Nullable
    private volatile BooleanExpression closed;

    @Nullable
    private volatile NumberExpression<Double> length;

    public MultiCurveExpression(Expression<T> mixin) {
        super(mixin);
    }

    /**
     * Returns 1 (TRUE) if this MultiCurve is closed [StartPoint ( ) = EndPoint
     * ( ) for each Curve in this MultiCurve].
     * 
     * @return
     */
    public BooleanExpression isClosed() {
        if (closed == null) {
            closed = BooleanOperation.create(SpatialOps.IS_CLOSED, mixin);
        }
        return closed;
    }

    /**
     * The Length of this MultiCurve which is equal to the sum of the lengths of
     * the element Curves.
     * 
     * @return
     */
    public NumberExpression<Double> length() {
        if (length == null) {
            length = NumberOperation.create(Double.class, SpatialOps.LENGTH,
                    mixin);
        }
        return length;
    }

}

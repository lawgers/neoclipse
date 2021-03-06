/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.neoclipse.action.layout;

import org.eclipse.jface.action.Action;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.neo4j.neoclipse.action.AbstractGraphAction;
import org.neo4j.neoclipse.action.Actions;
import org.neo4j.neoclipse.view.NeoGraphViewPart;

/**
 * This action sets the layout of the graph viewer to spring layout.
 * 
 * @author Peter H&auml;nsgen
 */
public class ShowSpringLayoutAction extends AbstractGraphAction
{
    /**
     * The constructor.
     */
    public ShowSpringLayoutAction( final NeoGraphViewPart view )
    {
        super( Actions.SPRING_LAYOUT, Action.AS_RADIO_BUTTON, view );
        setChecked( true );
    }

    /**
     * Executes the action.
     */
    @Override
    public void run()
    {
        if ( isChecked() )
        {
            graphView.getViewer().setLayoutAlgorithm(
                    new SpringLayoutAlgorithm(
                            LayoutStyles.NO_LAYOUT_NODE_RESIZING ), true );
        }
    }
}

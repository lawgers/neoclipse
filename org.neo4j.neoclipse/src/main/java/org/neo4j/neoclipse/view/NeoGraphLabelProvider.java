/*
 * NeoGraphLabelProvider.java
 */
package org.neo4j.neoclipse.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.NeoIcons;
import org.neo4j.neoclipse.action.ShowNodeColorsAction;
import org.neo4j.neoclipse.action.ShowNodeIconsAction;
import org.neo4j.neoclipse.action.ShowRelationshipDirectionsAction;
import org.neo4j.neoclipse.action.ShowNodeNamesAction;
import org.neo4j.neoclipse.action.ShowRelationshipColorsAction;
import org.neo4j.neoclipse.action.ShowRelationshipTypesAction;
import org.neo4j.neoclipse.preference.NeoPreferences;

/**
 * Provides the labels for graph elements.
 * @author Peter H&auml;nsgen
 * @author Anders Nawroth
 */
public class NeoGraphLabelProvider extends LabelProvider implements
    IConnectionStyleProvider, IColorProvider
{
    /**
     * Brightness of node background colors.
     */
    private static final float NODE_BRIGHTNESS = 1.0f;
    /**
     * Saturation of node background colors.
     */
    private static final float NODE_SATURATION = 0.15f;
    /**
     * Hue of the first node background color.
     */
    private static final float NODE_HUE = 240.0f;
    /**
     * Color of node foreground/text.
     */
    private static final Color NODE_FOREGROUND_COLOR = new Color( Display
        .getDefault(), new RGB( 0, 0, 0 ) );
    /**
     * Brightness of relationship stroke colors.
     */
    private static final float RELATIONSHIP_BRIGHTNESS = 0.8f;
    /**
     * Saturation of relationship stroke colors.
     */
    private static final float RELATIONSHIP_SATURATION = 0.9f;
    /**
     * Hue of the first relationship stroke color.
     */
    private static final float RELATIONSHIP_HUE = 60.0f;
    /**
     * Default node background color.
     */
    private static final Color NODE_BACKGROUND_COLOR = new Color( Display
        .getDefault(), new RGB( 255, 255, 255 ) );
    /**
     * Id of the reference node.
     */
    private static final long REFERENCE_NODE_ID = 0L;
    /**
     * The icon for nodes.
     */
    private Image nodeImage = NeoIcons.getImage( NeoIcons.NEO );
    /**
     * The icon for the root node.
     */
    private Image rootImage = NeoIcons.getImage( NeoIcons.NEO_ROOT );
    /**
     * User icons for nodes.
     */
    private NeoUserIcons userIcons = new NeoUserIcons();
    /**
     * Keep track of relationship types display on/off.
     */
    private boolean showRelationshipTypes = ShowRelationshipTypesAction.DEFAULT_STATE;
    /**
     * Keep track of relationship colors display on/off.
     */
    private boolean showRelationshipColors = ShowRelationshipColorsAction.DEFAULT_STATE;
    /**
     * Keep track of arrows display on/off.
     */
    private boolean showArrows = ShowRelationshipDirectionsAction.DEFAULT_STATE;
    /**
     * Keep track of node names display on/off.
     */
    private boolean showNames = ShowNodeNamesAction.DEFAULT_STATE;
    /**
     * Keep track of node icons display on/off.
     */
    private boolean showNodeIcons = ShowNodeIconsAction.DEFAULT_STATE;
    /**
     * Keep track of node colors display on/off.
     */
    private boolean showNodeColors = ShowNodeColorsAction.DEFAULT_STATE;
    /**
     * Map RelationshipTypes to Colors for the graph.
     */
    private Map<RelationshipType,Color> relationshipColors = new HashMap<RelationshipType,Color>();
    /**
     * Map node types to Colors for the graph.
     */
    private Map<String,Color> nodeColors = new HashMap<String,Color>();
    /**
     * Create colors for relationships.
     */
    private static NeoGraphColorGenerator relationshipColorGenerator;
    /**
     * Create colors for node.
     */
    private static NeoGraphColorGenerator nodeColorGenerator;
    /**
     * Location of node icons.
     */
    private static String nodeIconLocation;
    /**
     * Names of properties to look up for node labels.
     */
    private static ArrayList<String> nodePropertyNames;
    /**
     * Names of properties to look up for node icon names.
     */
    private static ArrayList<String> nodeIconPropertyNames;

    public NeoGraphLabelProvider()
    {
        // read all preferences
        readNodeIconLocation();
        readNodePropertyNames();
        readNodeIconPropertyNames();
        // refresh relationship colors
        refreshRelationshipColors();
    }

    /**
     * Returns the icon for an element.
     */
    public Image getImage( Object element )
    {
        if ( element instanceof Node )
        {
            Image img;
            Long id = ((Node) element).getId();
            if ( id.longValue() == REFERENCE_NODE_ID )
            {
                img = rootImage;
            }
            else
            {
                img = nodeImage;
            }
            if ( showNodeIcons && nodeIconLocation != "" )
            {
                for ( String propertyName : nodeIconPropertyNames )
                {
                    String tmpPropVal = (String) ((Node) element).getProperty(
                        propertyName, "" );
                    if ( tmpPropVal != "" ) // no empty strings
                    {
                        Image userImg = userIcons.getImage( tmpPropVal,
                            nodeIconLocation );
                        if ( userImg != null )
                        {
                            img = userImg;
                            break;
                        }
                    }
                }
            }
            return img;
        }
        return null;
    }

    /**
     * Returns the text for an element.
     */
    public String getText( Object element )
    {
        if ( element instanceof Node )
        {
            Node node = (Node) element;
            if ( !showNames || nodePropertyNames.size() == 0 )
            {
                // don't look for the default property
                if ( node.getId() == 0 )
                {
                    return ("Reference Node");
                }
                else
                {
                    return ("Node " + String.valueOf( ((Node) element).getId() ));
                }
            }
            else
            {
                // show the default property
                String propertyValue;
                if ( node.getId() == 0 )
                {
                    propertyValue = "Reference Node ";
                }
                else
                {
                    propertyValue = "Node ";
                }
                for ( String propertyName : nodePropertyNames )
                {
                    String tmpPropVal = (String) ((Node) element).getProperty(
                        propertyName, "" );
                    if ( tmpPropVal != "" ) // no empty strings
                    {
                        propertyValue = tmpPropVal + " #";
                        break;
                    }
                }
                return propertyValue
                    + String.valueOf( ((Node) element).getId() );
            }
        }
        else if ( element instanceof Relationship )
        {
            if ( showRelationshipTypes )
            {
                return ((Relationship) element).getType().toString() + " #"
                    + String.valueOf( ((Relationship) element).getId() );
            }
            else
            {
                return String.valueOf( ((Relationship) element).getId() );
            }
        }
        return element.toString();
    }

    /**
     * Remove relationship colors, start over creating new ones.
     */
    public static void refreshRelationshipColors()
    {
        relationshipColorGenerator = new NeoGraphColorGenerator(
            RELATIONSHIP_HUE, RELATIONSHIP_SATURATION, RELATIONSHIP_BRIGHTNESS );
    }

    /**
     * Read the location of node icons from preferences.
     */
    public static void readNodeIconLocation()
    {
        nodeIconLocation = Activator.getDefault().getPreferenceStore()
            .getString( NeoPreferences.NODE_ICON_LOCATION );
    }

    /**
     * Read the names of properties to look up for node labels from preferences.
     */
    public static void readNodePropertyNames()
    {
        String names = Activator.getDefault().getPreferenceStore().getString(
            NeoPreferences.NODE_PROPERTY_NAMES ).trim();
        nodePropertyNames = listFromString( names );
    }

    /**
     * Read the names of properties to look up for node icon names from
     * preferences.
     */
    public static void readNodeIconPropertyNames()
    {
        nodeColorGenerator = new NeoGraphColorGenerator( NODE_HUE,
            NODE_SATURATION, NODE_BRIGHTNESS );
        String names = Activator.getDefault().getPreferenceStore().getString(
            NeoPreferences.NODE_ICON_PROPERTY_NAMES ).trim();
        nodeIconPropertyNames = listFromString( names );
    }

    /**
     * Show or hide relationship types.
     * @param state
     *            set true to display
     */
    public void setShowRelationshipTypes( boolean state )
    {
        this.showRelationshipTypes = state;
    }

    /**
     * Show or hide relationship colors.
     * @param state
     *            set true to display
     */
    public void setShowRelationshipColors( boolean state )
    {
        this.showRelationshipColors = state;
    }

    /**
     * Show or hide arrows.
     * @param state
     *            set true to display
     */
    public void setShowArrows( boolean state )
    {
        this.showArrows = state;
    }

    /**
     * Show or hide names.
     * @param state
     *            set true to display
     */
    public void setShowNames( boolean state )
    {
        this.showNames = state;
    }

    /**
     * Show or hide node icons.
     * @param state
     *            set true to display
     */
    public void setShowNodeIcons( boolean state )
    {
        this.showNodeIcons = state;
    }

    /**
     * Show or hide node colors.
     * @param state
     *            set true to display
     */
    public void setShowNodeColors( boolean state )
    {
        this.showNodeColors = state;
    }

    @Override
    public Color getColor( Object rel )
    {
        if ( !showRelationshipColors )
        {
            return null;
        }
        RelationshipType type = ((Relationship) rel).getType();
        Color color = relationshipColors.get( type );
        if ( color == null )
        {
            color = relationshipColorGenerator.next();
            relationshipColors.put( type, color );
        }
        return color;
    }

    @Override
    public int getConnectionStyle( Object rel )
    {
        if ( showArrows )
        {
            return ZestStyles.CONNECTIONS_DIRECTED;
        }
        return 0;
    }

    @Override
    public Color getHighlightColor( Object rel )
    {
        return null;
    }

    @Override
    public int getLineWidth( Object rel )
    {
        return -1;
    }

    @Override
    public IFigure getTooltip( Object entity )
    {
        return null;
    }

    @Override
    public Color getBackground( Object element )
    {
        if ( element instanceof Node && showNodeColors )
        {
            for ( String propertyName : nodeIconPropertyNames )
            {
                String tmpPropVal = (String) ((Node) element).getProperty(
                    propertyName, "" );
                if ( tmpPropVal != "" ) // no empty strings
                {
                    Color color = nodeColors.get( tmpPropVal );
                    if ( color == null )
                    {
                        color = nodeColorGenerator.next();
                        nodeColors.put( tmpPropVal, color );
                    }
                    return color;
                }
            }
            return NODE_BACKGROUND_COLOR;
        }
        return null;
    }

    @Override
    public Color getForeground( Object element )
    {
        return NODE_FOREGROUND_COLOR;
    }

    /**
     * Convert a string containing a comma-separated list of names to a list of
     * strings. Ignores "" as a name.
     * @param names
     *            comma-separated names
     * @return list of names
     */
    private static ArrayList<String> listFromString( String names )
    {
        ArrayList<String> list = new ArrayList<String>();
        for ( String name : names.split( "," ) )
        {
            name = name.trim();
            if ( name == "" )
            {
                continue;
            }
            list.add( name );
        }
        return list;
    }
}
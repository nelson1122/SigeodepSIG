/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.util;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;

/**
 * The LineLegendItemSource class lets you create one of the items of a legend
 * in jfreechart graphs, to perform the custom mapping of color or pattern to a
 * particular series. This class is used by the indicators package.
 *
 * @author santos
 */
public class LineLegendItemSource implements LegendItemSource {
    /*esta clase permite crear uno de los items de una leyenda de
     los graficos jfreechart, para poder realizar la asignacion
     personalizada de color o trama a una determinada serie*/

    LegendItemCollection itemCollection = new LegendItemCollection();

    public LineLegendItemSource(LegendItemCollection li) {
        itemCollection = li;
    }

    @Override
    public LegendItemCollection getLegendItems() {
        return itemCollection;
    }
}

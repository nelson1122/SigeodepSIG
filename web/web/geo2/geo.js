/**
 * Copyright (c) 2008-2011 The Open Source Geospatial Foundation
 * 
 * Published under the BSD license.
 * See http://svn.geoext.org/core/trunk/geoext/license.txt for the full text
 * of the license.
 */

/** api: example[feature-grid]
 *  Grid with Features
 *  ------------------
 *  Synchronize selection of features between a grid and a layer.
 */

var mapPanel, mainPanel;
var bkey = "AnyGyd4GaAzToU0sDaA0NaXDD88yChcUh8ySoNc32_ddxkrxkl9K5SIATkA8EpMn";

Ext.onReady(function() {
    params = parseURLParams(window.location.href);
    createLegend(params["rf"][0]);
    vars = params["vars"][0];
    if (vars.indexOf('cuadrante') !== -1) {
        attribution = "<br>Policia Nacional de Colombia.<br>Plan Nacional de Vigilancia Comunitaria por Cuadrantes.<br>Sala CIEPS.";
    } else {
        attribution = "<br>Universidad de Nari&ntilde;o, Alcald&iacute;a Municipal de Pasto, Colciencias.";
    }

    initializeColumns(vars);
    var url_data = "data.jsp?" + window.location.href.split("?")[1];

    var map = new OpenLayers.Map();
    osm = new OpenLayers.Layer.OSM();
    hybrid = new OpenLayers.Layer.Bing({
        name: "Bing Hybrid",
        key: bkey,
        type: "AerialWithLabels"
    });
    aerial = new OpenLayers.Layer.Bing({
        name: "Bing Satellite",
        key: bkey,
        type: "Aerial"
    });
    // My code

    initialStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            fillColor: "#FFFFFF",
            'strokeWidth': 1,
            fillOpacity: 0.4,
            label : "${getLabel}",
            fontColor: "#555555",
            fontSize: "11px",
            fontFamily: "Arial",
            fontWeight: "bold",
            labelOutlineColor: "white",
            labelOutlineWidth: 3
        }, {
            context: {
                getLabel: function(feature) {
                    if(feature.layer.map.getZoom() > 15) {
                        if(feature.data.name !== null){
                            return feature.data.name;
                        } else {
                            return ""
                        }
                    } else {
                        return "";
                    }
                }
            }
        })
    });

    communeStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            fillColor: "#FFFFFF",
            'strokeWidth': 1,
            fillOpacity: 0.4,
            label : "${getLabel}",
            fontColor: "#555555",
            fontSize: "11px",
            fontFamily: "Arial",
            fontWeight: "bold",
            labelOutlineColor: "white",
            labelOutlineWidth: 3
        }, {
            context: {
                getLabel: function(feature) {
                    if(feature.layer.map.getZoom() <= 15 && feature.layer.map.getZoom() > 10) {
                        if(feature.data.name !== null){
                            return feature.data.name;
                        } else {
                            return ""
                        }
                    } else {
                        return "";
                    }
                }
            }
        })
    });

    quadrantStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            fillColor: "#FFFFFF",
            'strokeWidth': 1,
            fillOpacity: 0.0,
            label : "${getLabel}",
            fontColor: "black",
            fontSize: "10px",
            fontFamily: "Arial",
            fontWeight: "bold",
            labelOutlineColor: "white",
            labelOutlineWidth: 3
        }, {
            context: {
                getLabel: function(feature) {
                    if(feature.layer.map.getZoom() > 15) {
                        if(feature.data.name !== null){
                            return feature.data.name;
                        } else {
                            return ""
                        }
                    } else {
                        return "";
                    }
                }
            }
        })
    });

    customStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            fillColor: "\${colour}",
            'strokeWidth': 1,
            fillOpacity: 0.8,
            label : "${getLabel}",
            fontColor: "black",
            fontSize: "${getSize}",
            fontFamily: "Arial",
            fontWeight: "bold",
            labelOutlineColor: "white",
            labelOutlineWidth: 3
        }, {
            context: {
                getLabel: function(feature) {
                    if(geo_vars[index_g] === 'Comunas' || geo_vars[index_g] === 'Corredores'){
                        if(feature.layer.map.getZoom() > 12) {
                            if(feature.data.name !== null){
                                return feature.data.name;
                            } else {
                                return ""
                            }
                        } else {
                            return "";
                        }
                    } else if(geo_vars[index_g] === 'Cuadrantes'){
                        if(feature.layer.map.getZoom() > 12) {
                            if(feature.data.name !== null){
                                var the_name = feature.data.name
                                the_name = the_name.replace("E.CENTRO", "E.C");
                                the_name = the_name.replace("E.NORTE", "E.N");
                                the_name = the_name.replace("E.SUR", "E.S");
                                
                                return the_name;
                            } else {
                                return ""
                            }
                        } else {
                            return "";
                        }
                    } else {
                        return ""
                    }
                },
                getSize: function(feature){
                    if(geo_vars[index_g] === 'Cuadrantes'){
                        var the_size = feature.layer.map.getZoom() - 5;
                        return the_size + "px";
                    }
                    return "14px";
                }
            }
        })
    });

    selectionStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            fillColor: "#0000FF",
            'strokeWidth': 1,
            fillOpacity: 0.5
        })
    });

    vectors = new OpenLayers.Layer.Vector("Lesiones", {
        styleMap: customStyles,
        attribution: attribution,
        projection: new OpenLayers.Projection("EPSG:900913"),
        strategies: [new OpenLayers.Strategy.Fixed()],
        protocol: new OpenLayers.Protocol.HTTP({
            url: url_data,
            format: new OpenLayers.Format.GeoJSON(),
            callbackKey: "callback"
        }),
        eventListeners: {
            "featuresadded": function() {
                this.map.zoomToExtent(this.getDataExtent());
            }
        }
    });

    vectors.events.on({
        featureselected: function(e) {
            var html;
            if(geo_vars[index_g] === 'Barrios'){
                html = e.feature.data.suburb + '<br>' + e.feature.data.quadrant;
                if(e.feature.data.corridor !== 'SIN DATO'){
                    html += '<br>' + e.feature.data.corridor;
                }
                Ext.get('infopanel').set({
                    style: "font-size: 14;font-weight:bold;"
                });
                Ext.getCmp('infopanel').update(html);
            }
            if(geo_vars[index_g] === 'Cuadrantes'){
                html = e.feature.data.neighborhoods;
                html = html.replace('{','').replace('}','').replace(',',', ');
                Ext.get('infopanel').set({
                    style: "font-size: 12;"
                });
                Ext.getCmp('infopanel').update(html);
            }
            if (vars.length > 1) {
                WHERE = "(";
                for (var i = 0; i < this.selectedFeatures.length; i++) {
                    WHERE += "'" + this.selectedFeatures[i].data.name + "',";
                }
                WHERE = WHERE.substring(0, WHERE.length - 1) + ")";
                if(Ext.get('pie_window' + e.feature.data.id) === null){
                    var uri = "getPieData.jsp?geo_column=" + geo_column + "&column=" + column_a + "&WHERE=" + WHERE + "&" + window.location.href.split("?")[1];
                    OpenLayers.loadURL(uri, "", this, function onComplete(response) {
                        if (response.responseText.indexOf('EPIC FAIL!!!') === -1) {
                            var data_a = JSON.parse(response.responseText);
                            createPopup(e.feature);
                            setPie("piea_" + e.feature.data.id, data_a);
                            if (typeof index_b !== 'undefined') {
                                uri = "getPieData.jsp?geo_column=" + geo_column + "&column=" + column_b + "&WHERE=" + WHERE + "&" + window.location.href.split("?")[1];
                                OpenLayers.loadURL(uri, "", this, function onComplete(response) {
                                    if (response.responseText.indexOf('EPIC FAIL!!!') === -1) {
                                        var data_b = JSON.parse(response.responseText);
                                        setPie("pieb_" + e.feature.data.id, data_b);
                                    }
                                });
                            }
                            var x = -25;
                            var y = -75-legend.getSize().height;
                            popup.alignTo('mappanel', 'br-br', [x, y]);
                        }
                    });
                } else {
                    popup.destroy();
                }
            }
        }
    });

    function createPopup(feature) {
        if (typeof popup !== 'undefined' && Ext.get(popup.id) !== null) {
            if (typeof Ext.get(popup.id).select("div.x-tool-unpin").elements[0] !== 'undefined') {
                var flag = Ext.get(popup.id).select("div.x-tool-unpin").elements[0].style.display;
                if (flag !== 'none') {
                    popup.destroy();
                } 
            }
        }

        _feature = feature;
        _select = "<select id='sel_" + feature.data.id + "'>"
        + "<option value='1' selected> " + capitalise(vars[index_a]) + "</option>";
        if (typeof index_b !== 'undefined') {
            _select += "<option value='2'>" + capitalise(vars[index_b]) + "</option>";
        }
        _select += "</select>";

        popup = new GeoExt.Popup({
            id: 'pie_window' + feature.data.id,
            title: feature.data.name,
            anchored: false,
            location: feature,
            anchorPosition: "bottom-right",
            width: 0,
            html: _select + "<div id='piea_" + feature.data.id + "'></div><div id='pieb_" + feature.data.id + "'></div>",
            maximizable: true,
            collapsible: true,
            unpinnable: true
        });
        popup.show();
        Ext.get('sel_' + feature.data.id).on('change', this.onChange, this, {});
        Ext.get('piea_' + _feature.data.id).set({
            style: "position:absolute;top:'0px'"
        });
        Ext.get('pieb_' + _feature.data.id).set({
            style: "position:absolute;top:'0px'"
        });
        this.onChange();
        
    }

    onChange = function() {
        value = Ext.get('sel_' + _feature.data.id).dom.value;
        if (value === "1") {
            Ext.get('piea_' + _feature.data.id).fadeIn();
            Ext.get('pieb_' + _feature.data.id).fadeOut();
        } else {
            Ext.get('piea_' + _feature.data.id).fadeOut();
            Ext.get('pieb_' + _feature.data.id).fadeIn();

        }

    };
    blank = new OpenLayers.Layer.OSM("En Blanco","images/default/s.gif"); 

    map.addLayers([osm, hybrid, blank]);
    if(geo_vars[index_g] !== 'Cuadrantes'){
        neighborhoods = new OpenLayers.Layer.Vector("Barrios", {
            styleMap: initialStyles,
            projection: new OpenLayers.Projection("EPSG:900913"),
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP({
                url: "neighborhoods.jsp",
                format: new OpenLayers.Format.GeoJSON(),
                callbackKey: "callback"
            })
        });
        map.addLayer(neighborhoods);
    } else {
        quadrant_polygons = new OpenLayers.Layer.Vector(geo_vars[index_g], {
            styleMap: quadrantStyles,
            projection: new OpenLayers.Projection("EPSG:900913"),
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP({
                url: "features.jsp?features=" + geo_vars[index_g],
                format: new OpenLayers.Format.GeoJSON(),
                callbackKey: "callback"
            })
        });
        map.addLayer(quadrant_polygons);

    }
    if(geo_vars[index_g] === 'Comunas'){
        commune_polygons = new OpenLayers.Layer.Vector(geo_vars[index_g], {
            styleMap: communeStyles,
            projection: new OpenLayers.Projection("EPSG:900913"),
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP({
                url: "features.jsp?features=" + geo_vars[index_g],
                format: new OpenLayers.Format.GeoJSON(),
                callbackKey: "callback"
            })
        });
        map.addLayer(commune_polygons);   
    }
    map.addLayer(vectors);
    map.setLayerIndex(vectors, map.layers.length-2);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.MousePosition());
    getTitle(params["indicator_id"][0]);

    // create map panel
    mapPanel = new GeoExt.MapPanel({
        title: "Map ",
        id: "mappanel",
        region: "center",
        map: map
    });

    // create feature store, binding it to the vector layer
    var store = new GeoExt.data.FeatureStore({
        layer: vectors,
        fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'value',
            type: 'float'
        }
        ],
        autoLoad: true
    });

    // create grid panel configured with feature store
    gridPanel = new Ext.grid.GridPanel({
        title: geo_vars[index_g],
        region: "center",
        collapsible: true,
        store: store,
        width: 320,
        cm: new Ext.grid.ColumnModel([
        {
            id: "name",
            header: "Nombre",
            dataIndex: "name",
            sortable: true
        },
        {
            id: "value",
            header: "Valor",
            dataIndex: "value",
            sortable: true,
            direction: "desc"
        }
        ]),
        sm: new GeoExt.grid.FeatureSelectionModel(),
        autoExpandColumn: "name"
    });
    
    infoPanel = new Ext.Panel({
        region: "south",
        autoScroll: true,
        id: 'infopanel',
        height: 70
    });
    
    eastPanel = new Ext.Panel({
        layout: 'border',
        region: 'east',
        width: 320,
        items: [gridPanel, infoPanel]   
    });
    
    // create a panel and add the map panel and grid panel
    // inside it
    mainPanel = new Ext.Viewport({
        renderTo: "mainpanel",
        layout: "border",
        width: 1000,
        height: 800,
        items: [mapPanel, eastPanel]
    });

    legend = new Ext.Window({
        title: 'Legenda',
        collapsible: true,
        closable: false,
        layout: {
            type: 'absolute'
        },
        contentEl: 'legend'
    }).show().alignTo('mappanel', 'br-br', [-50, -50]);

});

function parseURLParams(url) {
    var queryStart = url.indexOf("?") + 1;
    var queryEnd = url.indexOf("#") + 1 || url.length + 1;
    var query = url.slice(queryStart, queryEnd - 1);

    if (query === url || query === "")
        return '';

    var params = {};
    var nvPairs = query.replace(/\+/g, " ").split("&");

    for (var i = 0; i < nvPairs.length; i++) {
        var nv = nvPairs[i].replace("%E1","&aacute").replace("%E9","&eacute").replace("%ED","&iacute").replace("%F3","&oacute").replace("%FA","&uacute").replace("%F1","&ntilde").split("=");
        var n = decodeURIComponent(nv[0]);
        var v = decodeURIComponent(nv[1]);
        if (!(n in params)) {
            params[n] = [];
        }
        params[n].push(nv.length === 2 ? v : null);
    }
    return params;
}

function setPie(id, data) {
    var r = new Raphael(id);
    var radio = 60;
    var y = 0;
    if (radio < data.values.length * 10) {
        y = 5 + data.values.length * 10;
    } else {
        y = radio + 5;
    }
    var pie = r.piechart(radio + 5, y, radio, data.values,
    {
        legend: data.labels,
        legendpos: "east"
    });
    pie.hover(function() {
        this.sector.stop();
        this.sector.scale(1.1, 1.1, this.cx, this.cy);

        if (this.label) {
            this.label[0].stop();
            this.label[0].attr({
                r: 7.5
            });
            this.label[1].attr({
                "font-weight": 800
            });
        }
    }, function() {
        this.sector.animate({
            transform: 's1 1 ' + this.cx + ' ' + this.cy
        }, 500, "bounce");

        if (this.label) {
            this.label[0].animate({
                r: 5
            }, 500, "bounce");
            this.label[1].attr({
                "font-weight": 400
            });
        }
    });
    var width = popup.getSize().width;
    if (width < pie.getBBox().x2 + 50) {
        width = pie.getBBox().x2 + 50;
    }
    var height = popup.getSize().height;
    if (height < pie.getBBox().y2 + 60) {
        height = pie.getBBox().y2 + 60;
    }
    popup.setSize(width, height);
}

function initializeColumns(columns) {
    columns = columns.substr(0, columns.length - 1);
    vars = columns.split(',');
    geo_vars = [];
    var flag = true;
    for (var i = 0; i < vars.length; i++) {
        if (vars[i] === 'barrio' || vars[i] === 'comuna' || vars[i] === 'cuadrante' || vars[i] === 'corredor') {
            geo_column = 'column_' + (i + 1);
            index_g = i;
            if(vars[i] === 'corredor'){
                geo_vars[index_g] = capitalise(vars[i] + 'es');
            } else {
                geo_vars[index_g] = capitalise(vars[i] + 's');
            }
        } else {
            if (flag) {
                column_a = 'column_' + (i + 1);
                index_a = i;
                flag = false;
            } else {
                column_b = 'column_' + (i + 1);
                index_b = i;
            }
        }
    }
}

function capitalise(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function getTitle(indicator_id){
    Ext.Ajax.request({
        url: 'getMapName.jsp?indicator_id=' + indicator_id,//replace with ajax call
        success: function(response, opts) {
            map_info = JSON.parse(response.responseText);
            mapPanel.setTitle(map_info.title);
        },
        failure: function(response, opts){
            return "Fail!!!"
        }
    });
}

function createLegend(rf) {
    var paper = Raphael(document.getElementById('legend'), 320, 320);
    var ranges = rf.split("<end>");
    var nranges = ranges.length - 1;
    var width = 0;
    var x = 15;
    for (var i = 0; i < nranges; i++) {
        var range = ranges[i].split("<tab>");
        var y = 15 + (i * 25);
        paper.circle(x, y, 10).attr(
        {
            "fill": "#" + range[0],
            "stroke": "#000000"
        });
        var range_text = paper.text(x + 25, y, range[2]).attr(
        {
            "fill": "#000",
            "font-size": 16,
            "text-anchor": "start",
            "font-family": "Arial, Helvetica, sans-serif"
        });
        if (width < range_text.getBBox().width) {
            width = range_text.getBBox().width;
        }
    }
    paper.setSize(x + 30 + width, 10 + (nranges * 25));
}
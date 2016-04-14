/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var map; //contenedor de capas y componentes
var pointsOverlay, heatmapOverlay; //Grupos de capas para puntos / heatmap
var container, content, closer; // contenedores (divs) para el popup (mapa de puntos)
var popup, singleclickFunction, pointermoveFunction; //variables/eventos(funciones) necesarios para ejecutar popup
var drawingLayer, drawInteraction;
var selectClick;

$(function () {

    container = document.getElementById('popup');
    content = document.getElementById('popup-content');
    closer = document.getElementById('popup-closer');

    var bmapsRoads = new ol.layer.Tile({
        title: 'BingMaps Roads',
        source: new ol.source.BingMaps({
            key: 'AnyGyd4GaAzToU0sDaA0NaXDD88yChcUh8ySoNc32_ddxkrxkl9K5SIATkA8EpMn',
            imagerySet: 'Road'
        }),
        visible: false
    });
    /*Creacion capa base Bing Maps Aerial*/
    var bmapsAerial = new ol.layer.Tile({
        title: 'BingMaps Aerial',
        source: new ol.source.BingMaps({
            key: 'AnyGyd4GaAzToU0sDaA0NaXDD88yChcUh8ySoNc32_ddxkrxkl9K5SIATkA8EpMn',
            imagerySet: 'AerialWithLabels'}),
        visible: false
    });
    /*Creacion capa base de OSM*/
    var osmLayer = new ol.layer.Tile({
        title: 'OpenStreetMaps',
        source: new ol.source.OSM(),
        visible: false
    });

    /**
     * GRUPOS PARA LAYERSWITCHER
     * @type ol.layer.Group
     */
    var baseMaps = new ol.layer.Group({
        title: 'BaseMaps',
        layers: [bmapsRoads, bmapsAerial, osmLayer]
    });

    /**
     * MAPA
     * @type ol.Map
     */
    map = new ol.Map({
        target: 'map', // The DOM element that will contains the map

        controls: ol.control.defaults({
            attributionOptions: ({
                collapsible: false
            })
        }).extend([
            new ol.control.ZoomToExtent({
                extent: [
                    -8605605, 137132,
                    -8599605, 132374
                ]
            }),
            new ol.control.ScaleLine(),
            //new ol.control.FullScreen(),
            new ol.control.MousePosition({
                coordinateFormat: ol.coordinate.createStringXY(4),
                projection: 'EPSG:3857'
            })
        ]),
        renderer: 'canvas', // Force the renderer to be used
        layers: [baseMaps],
        // Create a view centered on the specified location and zoom level
        view: new ol.View({
            center: [-8602509.5692, 134983.3435],
            zoom: 14,
            maxZoom: 18,
            minZoom: 14
        })
    });

    //Capa donde se dibujarán las areas de interes
    drawingLayer = new ol.layer.Vector({
        source: new ol.source.Vector({wrapX: false}),
        style: new ol.style.Style({
            fill: new ol.style.Fill({
                color: 'rgba(0, 0, 255, 0.2)'
            }),
            stroke: new ol.style.Stroke({
                color: '#2E9AFE',
                width: 2
            }),
            image: new ol.style.Circle({
                radius: 7,
                fill: new ol.style.Fill({
                    color: '#ffcc33'
                })
            })
        })
    });
    map.addLayer(drawingLayer);

    /**
     * ADICION DEL CONTROL LAYERSWITCHER
     * @type ol.control.LayerSwitcher
     */
    var layerSwitcher = new ol.control.LayerSwitcher();
    map.addControl(layerSwitcher);

    setInterval(function () {
        map.updateSize();
    }, 2);

    loadDataLayer();

});

/**
 * Metodo encargado de cargar la capa de datos de acuerdo a la seleccion del usuario:
 * Mapa de puntos / Mapa de calor
 * @returns {undefined}
 */
function loadDataLayer() {

    removeComponents();
    drawingLayer.getSource().clear();
    map.removeLayer(heatmapOverlay);
    map.removeLayer(pointsOverlay);


    var data = $('#formInjuries\\:txtGeoJSON').val();
    var showInjuriesLayer = $('#formInjuries\\:boolShowInjuriesLayer').val();
    var mapType = $('#formInjuries\\:txtMapType').val();

    if (showInjuriesLayer !== 'false' && mapType !== '') {



        var geoJSONSource = new ol.source.Vector({
            features: (new ol.format.GeoJSON()).readFeatures(data)
        });

        if (mapType === 'points') {
            loadPoints(geoJSONSource);
        } else
        if (mapType === 'heatmap') {
            loadHeatmap(geoJSONSource);
        }
    }

}

/**
 * Metodo encargado de cargar la capa de puntos y sus componentes (funcionalidades)
 * correspondientes
 * @param {ol.source.DataSource} dataSource
 * @returns {loadPoints}
 */
function loadPoints(dataSource) {

    var points = new ol.layer.Vector({
        title: 'Puntos',
        source: dataSource,
        style: new ol.style.Style({
            image: new ol.style.Icon({
                anchor: [190, 370],
                anchorXUnits: 'pixels',
                anchorYUnits: 'pixels',
                src: 'img/location_icon-1.png',
                scale: 0.10
            })
        }),
        visible: true
    });

    pointsOverlay = new ol.layer.Group({
        title: 'Capas de Datos',
        layers: [points]
    });


    /**
     * DIALOGO DE CARACTERISTICAS (POPUP)
     */
    //Add a click handler to hide the popup.
    closer.onclick = function () {
        container.style.display = 'none';
        closer.blur();
        return false;
    };

    // Create an overlay to anchor the popup to the map.
    popup = new ol.Overlay({
        element: container
    });

    map.addOverlay(popup);

    singleclickFunction = function (evt) {

        //alert('Entro al listener');
        var feature = map.forEachFeatureAtPixel(evt.pixel, function (feature) {
            return feature;
        });

        if (feature) {
            popup.setPosition(evt.coordinate);

            //var coordinates = feature.getGeometry().getCoordinates();
            var properties = feature.getProperties();
            var injury_id = '';
            for (var key in properties) {
                if (key === 'fatal_injury_id' || key === 'non_fatal_injury_id') {
                    //alert("Key: "+ key + ", Value: " + properties[key]);
                    injury_id = properties[key];
                }
            }


            html = '<b>injury id: ' + injury_id + '</br>';
            content.innerHTML = html;
            container.style.display = 'block';
        }
    };

    map.on('singleclick', singleclickFunction);

    /*
     * PUNTERO (EVENTO QUE SE EJECUTA AL PASAR EL PUNTERO SOBRE LOS PUNTOS GEOCODIFICADOS)
     * @type String
     */
    var cursorHoverStyle = "pointer";
    var target = map.getTarget();

    //target returned might be the DOM element or the ID of this element dependeing on how the map was initialized
    //either way get a jQuery object for it
    var jTarget = typeof target === "string" ? $("#" + target) : $(target);

    pointermoveFunction = function (event) {
        var mouseCoordInMapPixels = [event.originalEvent.offsetX, event.originalEvent.offsetY];

        //detect feature at mouse coords
        var hit = map.forEachFeatureAtPixel(mouseCoordInMapPixels, function (feature, layer) {
            return true;
        });

        if (hit) {
            jTarget.css("cursor", cursorHoverStyle);
        } else {
            jTarget.css("cursor", "");
        }
    };

    map.on("pointermove", pointermoveFunction);

    //Si todo se ejecuta correctamente, se agrega la capa de puntos
    map.addLayer(pointsOverlay);

}


/**
 * Metodo encargado de cargar el mapa de calor con sus respectivos componentes 
 * (funcionalidades)
 * @param {type} dataSource
 * @returns {undefined}
 */
function loadHeatmap(dataSource) {

    var heatmap = new ol.layer.Heatmap({
        title: 'Heatmap',
        source: dataSource,
        visible: true,
        blur: 10,
        radius: 10
    });

    heatmapOverlay = new ol.layer.Group({
        title: 'Capas de Datos',
        layers: [heatmap]
    });

    changeInteraction();

    map.addLayer(heatmapOverlay);
}

/**
 * Metodo encargado de cambiar la interaccion del usuario con el mapa de calor
 * Dibujar areas de interes/Seleccionar areas de interes
 * @returns {undefined}
 */
function changeInteraction() {
    var drawOption = $('#formInjuries\\:boolDrawOption').val();
    var selectOption = $('#formInjuries\\:boolSelectOption').val();

    map.removeInteraction(selectClick);
    map.removeInteraction(drawInteraction);


    if (drawOption !== 'false' && selectOption !== 'true') {//Se ha seleccionado la opcion Dibujar areas

        /**
         * DIBUJAR AREAS
         */
        function addInteraction() {
            geometryFunction = function (coordinates, geometry) {
                if (!geometry) {
                    geometry = new ol.geom.Polygon(null);
                }
                var start = coordinates[0];
                var end = coordinates[1];
                geometry.setCoordinates([
                    [start, [start[0], end[1]], end, [end[0], start[1]], start]
                ]);
                return geometry;
            };
            drawInteraction = new ol.interaction.Draw({
                source: drawingLayer.getSource(),
                type: 'LineString',
                geometryFunction: geometryFunction,
                maxPoints: 2,
                layers: [drawingLayer]
            });
            map.addInteraction(drawInteraction);
        }
        addInteraction();

    } else
    if (selectOption !== 'false' && drawOption !== 'true') { //Se ha seleccionado la opcion Seleccionar areas
        selectClick = new ol.interaction.Select({
            condition: ol.events.condition.click,
            source: drawingLayer.getSource(),
            layers: [drawingLayer]
        });

        selectClick.on('select', function (evt) {
            var selected = evt.selected;
            var deselected = evt.deselected;

            if (selected.length) {
                selected.forEach(function (feature) {
                    //console.info("SELECCIONADO");
                    //console.info(feature.getGeometry().getCoordinates());
                    var coordinates = feature.getGeometry().getCoordinates()[0];

                    if (coordinates.length) {
                        var coord = "Es poligono: \n"
                                + "1." + coordinates[0] + "\n"
                                + "2." + coordinates[1] + "\n"
                                + "3." + coordinates[2] + "\n"
                                + "4." + coordinates[3] + "\n"
                                + "5." + coordinates[4] + "\n";
                        alert(coord);
                    }
                });
            } else {
                deselected.forEach(function (feature) {
                    //console.info("NO SELECCIONADO");
                    //console.info(feature);

                });
            }
        });

        map.addInteraction(selectClick);
    }
}


/**
 * Metodo encargado de eliminar todos los posibles componentes/eventos cargados al mapa. 
 * @returns {undefined}
 */
function removeComponents() {
    //Componentes del mapa de puntos
    map.removeOverlay(popup);
    map.un('singleclick', singleclickFunction);
    map.un('pointermove', pointermoveFunction);

    //Componentes del mapa de calor
    map.removeInteraction(drawInteraction);
    map.removeInteraction(selectClick);
}

/**
 * Metodo encargado de limpiar las figuras dibujadas sobre el mapa, y tambien 
 * las interacciones utilizadas
 * @returns {undefined}
 */

function clearLayer(){
    drawingLayer.getSource().clear();
    if (typeof selectClick !== 'undefined'){
        selectClick.getFeatures().clear();
    }
}

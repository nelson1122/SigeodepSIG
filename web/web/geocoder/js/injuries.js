/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var map; //contenedor de capas y componentes
var points, heatmap;
var pointsOverlay, heatmapOverlay; //Grupos de capas para puntos / heatmap
var container, content, closer; // contenedores (divs) para el popup (mapa de puntos)
var popup, singleclickFunction, pointermoveFunction; //variables/eventos(funciones) necesarios para ejecutar popup
var drawingLayer, drawInteraction;
var selectClick;
var chart;
var geoserverHost;

$(function () {

    //Divisiones para popup (configuradas con estilos)
    container = document.getElementById('popup');
    content = document.getElementById('popup-content');
    closer = document.getElementById('popup-closer');

    geoserverHost = 'http://' + location.host + '/geoserver/geocoder/wms';

    var bmapsRoads = new ol.layer.Tile({
        title: 'BingMaps Roads',
        source: new ol.source.BingMaps({
            key: 'AnyGyd4GaAzToU0sDaA0NaXDD88yChcUh8ySoNc32_ddxkrxkl9K5SIATkA8EpMn',
            imagerySet: 'Road'
        }),
        visible: true
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
        title: 'Mapas Base',
        layers: [osmLayer, bmapsRoads, bmapsAerial]
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
            minZoom: 13
        })
    });


    //Creacion de las capas de puntos y de calor

    points = new ol.layer.Image({
        title: 'Points',
        source: new ol.source.ImageWMS({
            ratio: 1,
            url: geoserverHost,
            params: {
                FORMAT: 'image/png',
                VERSION: '1.1.1',
                LAYERS: 'geocoder:geocoded_injuries-points',
                STYLES: '',
                viewparams: '',
                env:''

            }
        })
    });

    heatmap = new ol.layer.Image({
        title: 'Heatmap',
        source: new ol.source.ImageWMS({
            ratio: 1,
            url: geoserverHost,
            params: {
                FORMAT: 'image/png',
                VERSION: '1.1.1',
                LAYERS: 'geocoder:geocoded_injuries-heatmap',
                STYLES: '',
                viewparams: '',
                env: ''
            }
        })
    });

    console.log($('#formInjuries\\:heatmapOpacity').val());

    pointsOverlay = new ol.layer.Group({
        title: 'WMS',
        layers: [points]
    });

    heatmapOverlay = new ol.layer.Group({
        title: 'WMS',
        layers: [heatmap]
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

    /*
     * Controles para configuracion de capas
     * @returns {undefined}
     */
    var pointsOpacity = $('#formInjuries\\:pointsOpacity').val();
    var heatmapOpacity = $('#formInjuries\\:heatmapOpacity').val();
    var heatmapRadius = $('#formInjuries\\:heatmapRadius').val();

    $('#pointsOpacityLabel').text(pointsOpacity);
    $('#heatmapOpacityLabel').text(heatmapOpacity);
    $('#heatmapRadiusLabel').text(heatmapRadius);
    
    $("#pointsOpacity").slider({
        max: 1,
        min: 0,
        step: 0.01,
        value: pointsOpacity,
        slide: function (event, ui) {
            $('#pointsOpacityLabel').text(parseFloat(ui.value).toFixed(2));
            $('#formInjuries\\:pointsOpacity').val(ui.value);
            
            var source = points.getSource();
            var params = source.getParams();
            params['env'] = 'opacity:' + ui.value;
            source.updateParams(params);

            updateLayerStyle();
        }
    });
    
    
    $("#heatmapOpacity").slider({
        max: 1,
        min: 0,
        step: 0.01,
        value: heatmapOpacity,
        slide: function (event, ui) {
            $('#heatmapOpacityLabel').text(parseFloat(ui.value).toFixed(2));
            $('#formInjuries\\:heatmapOpacity').val(ui.value);
            
            var source = heatmap.getSource();
            var params = source.getParams();
            params['env'] = 'radius:'+ $('#formInjuries\\:heatmapRadius').val() +';opacity:' + ui.value;
            source.updateParams(params);

            updateLayerStyle();
        }
    });
    
    $("#heatmapRadius").slider({
        max: 20,
        min: 2,
        step: 1,
        value: heatmapRadius,
        slide: function (event, ui) {
            $('#heatmapRadiusLabel').text(ui.value);
            $('#formInjuries\\:heatmapRadius').val(ui.value);
            
            var source = heatmap.getSource();
            var params = source.getParams();
            params['env'] = 'radius:'+ ui.value +';opacity:'+ $('#formInjuries\\:heatmapOpacity').val();
            source.updateParams(params);

            updateLayerStyle();
        }
    });
    
    /*
     * Carga de capas de datos
     * @returns {undefined}
     */

    loadDataLayer();

});

/**
 * Metodo encargado de cargar la capa de datos de acuerdo a la seleccion del usuario:
 * Mapa de puntos / Mapa de calor
 * @param {String} data description
 * @returns {undefined}
 */
function loadDataLayer() {

    removeComponents();
    drawingLayer.getSource().clear();
    map.removeLayer(heatmapOverlay);
    map.removeLayer(pointsOverlay);

    var geoserverParams = $('#formInjuries\\:txtGeoserverParameters').val();
    var showInjuriesLayer = $('#formInjuries\\:boolShowInjuriesLayer').val();
    var mapType = $('#formInjuries\\:txtMapType').val();

    //alert(data);

    if (showInjuriesLayer !== 'false' && mapType !== '') {

        if (mapType === 'points') {
            loadPoints(geoserverParams);
        } else
        if (mapType === 'heatmap') {
            loadHeatmap(geoserverParams);
        }

    }

}

/**
 * Metodo encargado de cargar la capa de puntos y sus componentes (funcionalidades)
 * correspondientes
 * @param {ol.source.DataSource} dataSource
 * @returns {loadPoints}
 */

function loadPoints(geoserverParams) {
    
    var pointsOpacity = $('#formInjuries\\:pointsOpacity').val();

    var source = points.getSource();
    var params = source.getParams();
    params['viewparams'] = geoserverParams;
    params['env'] = 'opacity:'+ pointsOpacity;
    params.t = new Date().getMilliseconds();
    source.updateParams(params);

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
        var view = map.getView();
        var viewResolution = view.getResolution();
        var source = points.getSource();
        var url = source.getGetFeatureInfoUrl(
                evt.coordinate,
                viewResolution,
                view.getProjection(),
                {
                    'INFO_FORMAT': 'application/json',
                    'FEATURE_COUNT': 1
                }
        );
        if (url) {
            $.getJSON(url, function (data) {
                if (data.features.length > 0) {
                    popup.setPosition(evt.coordinate);
                    var injury_id = data.features[0].properties.injury_id;

                    $('#formInjuries\\:txtInjuryIdForInfo').val(injury_id);

                    var html = "";
                    content.innerHTML = html;
                    container.style.display = 'block';

                    loadPointInfo();
                }
            });
        }
    };

    map.on('singleclick', singleclickFunction);

    //Si todo se ejecuta correctamente, se agrega la capa de puntos
    map.addLayer(pointsOverlay);

}


/**
 * Metodo encargado de cargar el mapa de calor con sus respectivos componentes 
 * (funcionalidades)
 * @param {type} dataSource
 * @returns {undefined}
 */
function loadHeatmap(geoserverParams) {

    var heatmapOpacity = $("#formInjuries\\:heatmapOpacity").val();
    var heatmapRadius = $("#formInjuries\\:heatmapRadius").val();
    
    console.log(heatmapOpacity);
    console.log(heatmapRadius);

    var source = heatmap.getSource();
    var params = source.getParams();
    params['viewparams'] = geoserverParams;
    params['env'] = 'opacity:'+ heatmapOpacity +';radius:'+ heatmapRadius;
    params.t = new Date().getMilliseconds();
    source.updateParams(params);

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
                        var coord = ""
                                + coordinates[0][0] + " " + coordinates[0][1] + ","
                                + coordinates[1][0] + " " + coordinates[1][1] + ","
                                + coordinates[2][0] + " " + coordinates[2][1] + ","
                                + coordinates[3][0] + " " + coordinates[3][1] + ","
                                + coordinates[4][0] + " " + coordinates[4][1];
                        $('#formInjuries\\:txtSelectedBox').val(coord);
                        remoteDataProcess();
                    }
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
function clearLayer() {

    drawingLayer.getSource().clear();
    if (typeof selectClick !== 'undefined') {
        selectClick.getFeatures().clear();
    }
    removeComponents();
}

function updateHeatmapStyle() {
    /*
     var blur = $("#formInjuries\\:blurValueOutput").text();
     var radio = $("#formInjuries\\:radioValueOutput").text();
     
     heatmap.setBlur(parseInt(blur), 10);
     heatmap.setRadius(parseInt(radio), 10);
     */
}
/**
 * Metodo encargado de cargar las estructuras JSON creadas en el Managed Bean InjuriesCountMB
 * y crea el grafico respectivo
 * @returns {undefined}
 */

function loadChart() {

    var categoryAxis = $('#formInjuries\\:txtCategoryAxis').val();
    var seriesValues = $('#formInjuries\\:txtSeries').val();
    var graphicTitle = $('#formInjuries\\:txtGraphicTitle').val();
    var categoyAxisLabel = $('#formInjuries\\:txtCategoryAxisLabel').val();

    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'container', // Le doy el nombre a la gráfica
            defaultSeriesType: 'column'	// Pongo que tipo de gráfica es
        },
        title: {
            text: graphicTitle	// Titulo (Opcional)
        },
        subtitle: {
            text: ''		// Subtitulo (Opcional)
        },
        // Pongo los datos en el eje de las 'X'
        xAxis: {
            categories: JSON.parse(categoryAxis),
            // Pongo el título para el eje de las 'X'
            title: {
                text: categoyAxisLabel
            }
        },
        yAxis: {
            // Pongo el título para el eje de las 'Y'
            title: {
                text: 'Nro de delitos'
            }
        },
        // Doy formato al la "cajita" que sale al pasar el ratón por encima de la gráfica
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y} delitos</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        series: JSON.parse(seriesValues)
    });

}
/**
 * Metodo encargado de cargar la informacion del punto seleccionado una vez se haya procesado
 * los datos que seran visualizados en el popup(llamada a procedimiento en ManagedBean)
 * @returns {undefined}
 */
function loadInfo() {
    var info = $('#formInjuries\\:txtPopupInfo').val();
    console.log(info);

    content.innerHTML = info;
    container.style.display = 'block';
}
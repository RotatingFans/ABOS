import React, {Component} from 'react';
import GoogleMapReact from 'google-map-react';
import {mapKey} from "./mapKey";
import restClient from '../grailsRestClient';
import {addField, fetchUtils, GET_LIST, GET_MANY, GET_ONE, Responsive, ViewTitle} from 'react-admin';
import supercluster from "points-cluster";
import SimpleMarker from "./markers/SimpleMarker";
import ClusterMarker from "./markers/ClusterMarker";


const httpClient = (url, options = {}) => {
    if (!options.headers) {
        options.headers = new Headers({Accept: 'application/json'});
    }
    const token = localStorage.getItem('access_token');
    options.headers.set('Authorization', `Bearer ${token}`);
    return fetchUtils.fetchJson(url, options);
};
const dataProvider = restClient(httpClient);
const MapMarker = ({text, customer}) => <div>{customer.customerName}</div>;

class SimpleMap extends Component {
    static defaultProps = {
        center: {
            lat: 59.95,
            lng: 30.33
        },
        zoom: 11,
        clusterRadius: 60,
        hoverDistance: 30,
        options: {
            minZoom: 3,
            maxZoom: 15,
        },
        style: {
            position: 'relative',
            margin: 0,
            padding: 0,
            flex: 1,
        },
    };
    state = {
        mapProps: {
            center: {
                lat: 0,
                lng: 0
            },
            zoom: 11,
        },
        markers: [],
        customers: [],
        clusterRadius: 30,
        options: {
            minZoom: 3,
            maxZoom: 19,
        },
        getCluster: (e) => {
        },
        clusters: [],
        hoveredMarkerId: {},
        hoverDistance: 30,



    };
    getCustomers = () => {

        dataProvider(GET_LIST, 'customers', {
            filter: {},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            let nCustomers = 1;
            let lat = 0;
            let lng = 0;
            response.data.forEach(customer => {
                lat += customer.latitude;
                lng += customer.longitude;
            });
            if (response.data.length > 0) {
                nCustomers = response.data.length;
            }
            let mapMarkers = [];

            response.data.forEach(customer => {
                /*mapMarkers.push(<MapMarker key={customer.id} lat={customer.latitude} lng={customer.longitude}
                                           customer={customer}/>);*/
                mapMarkers.push({
                    id: customer.id,
                    lat: customer.latitude,
                    lng: customer.longitude
                });

            });

            this.setState({
                customers: response.data,
                mapProps: {center: {lat: lat / nCustomers, lng: lng / nCustomers}, zoom: 11},
                markers: mapMarkers
            });
        })
    };
    getMapMarkers = () => {

    };

    componentWillMount() {
        this.getCustomers();
    }

    setHoveredMarkerId = (id) => {
        this.setState({hoveredMarkerId: id});

    };

    setMapProps = ({center, zoom, bounds}) => {
        this.setState({mapProps: {center, zoom, bounds}});
    };
    onChange = ({center, zoom, bounds}) => {
        if (center !== this.state.mapProps.center || zoom !== this.state.mapProps.zoom || bounds !== this.state.mapProps.bounds) {
            this.setMapProps({center, zoom, bounds});
        }
    };

    onChildMouseEnter = (hoverKey, {id}) => {

        this.setHoveredMarkerId(id);
    };

    onChildMouseLeave = (/* hoverKey, childProps */) => {

        this.setHoveredMarkerId(-1);
    };

    componentDidMount() {

    }


    componentDidUpdate(prevProps, prevState) {
        /* if (this. state.markers !== prevState.markers) {
             const { markers = [], clusterRadius, options: { minZoom, maxZoom } } = this.state;
             this.setState({getCluster: supercluster(
                     markers,
                     {
                         minZoom, // min zoom to generate clusters on
                         maxZoom, // max zoom level to cluster the points on
                         radius: clusterRadius, // cluster radius in pixels
                     }
                     )
                 }
             )


         }*/
        if (this.state.mapProps !== prevState.mapProps || this.state.markers !== prevState.markers) {
            const {mapProps, getCluster} = this.state;
            this.setState({
                    clusters: mapProps.bounds
                        ? supercluster(
                            this.state.markers,
                            {
                                minZoom: this.state.options.minZoom, // min zoom to generate clusters on
                                maxZoom: this.state.options.maxZoom, // max zoom level to cluster the points on
                                radius: this.state.clusterRadius, // cluster radius in pixels
                            })(mapProps)
                            .map(({wx, wy, numPoints, points}) => ({
                                lat: wy,
                                lng: wx,
                                text: numPoints,
                                numPoints,
                                id: `${numPoints}_${points[0].id}`,
                            }))
                        : [],
                }
            )


        }
        if (this.state.clusters !== prevState.clusters && this.state.hoveredMarkerId !== prevState.hoveredMarkerId) {
            const {clusters, hoveredMarkerId} = this.state;
            this.setState({
                    clusters: clusters
                        .map(({...cluster, id}) => ({
                            ...cluster,
                            hovered: id === hoveredMarkerId,
                        })),
                }
            )


        }

    }



    render() {
        const {
            style, hoverDistance, options,
            mapProps: {center, zoom},
            clusters,
        } = this.state;
        return (
            <div style={{height: '100vh', width: '100%'}}>

                <GoogleMapReact
                    options={options}
                    hoverDistance={hoverDistance}
                    center={center}
                    zoom={zoom}
                    onChange={this.onChange}
                    // onChildMouseEnter={this.onChildMouseEnter}
                    // onChildMouseLeave={this.onChildMouseLeave}
                    bootstrapURLKeys={{key: mapKey}}

                >
                    {
                        clusters
                            .map(({...markerProps, id, numPoints}) => (
                                numPoints === 1
                                    ? <SimpleMarker key={id} {...markerProps} />
                                    : <ClusterMarker key={id} {...markerProps} />
                            ))
                    }
                </GoogleMapReact>
            </div>
        )
        /* return (
             // Important! Always set the container height explicitly
             <div style={{height: '500px', width: '100%'}}>

                 <GMap mapProps={{center: this.state.center,
                     zoom: this.props.zoom}} markers={this.getMapMarkers()}/>
                 {/!*<GoogleMapReact
                     bootstrapURLKeys={{key: mapKey}}
                     defaultCenter={this.props.center}
                     defaultZoom={this.props.zoom}
                     center={this.state.center}
                 >
                     {this.getMapMarkers()}
                     <MapMarker
                         lat={59.955413}
                         lng={30.337844}
                         text={'Kreyser Avrora'}
                     />
                 </GoogleMapReact>*!/}
             </div>
         );*/
    }
}

export default SimpleMap;
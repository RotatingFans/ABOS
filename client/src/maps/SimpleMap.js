import React, {Component} from 'react';
import GoogleMapReact from 'google-map-react';
import {mapKey} from "./mapKey";
import restClient from '../grailsRestClient';
import {addField, fetchUtils, GET_LIST, GET_MANY, GET_ONE, Responsive, ViewTitle} from 'react-admin';


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
        zoom: 11
    };
    state = {
        center: {
            lat: 0,
            lng: 0
        },
        zoom: 11,
        customers: []
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
            this.setState({customers: response.data, center: {lat: lat / nCustomers, lng: lng / nCustomers}});
        })
    };
    getMapMarkers = () => {
        let mapMarkers = [];

        this.state.customers.forEach(customer => {
            mapMarkers.push(<MapMarker key={customer.id} lat={customer.latitude} lng={customer.longitude}
                                       customer={customer}/>);

        });

        return mapMarkers;

    };

    componentWillMount() {
        this.getCustomers();
    }

    render() {
        return (
            // Important! Always set the container height explicitly
            <div style={{height: '100vh', width: '100%'}}>
                <GoogleMapReact
                    bootstrapURLKeys={{key: mapKey}}
                    defaultCenter={this.props.center}
                    defaultZoom={this.props.zoom}
                    center={this.state.center}
                >
                    {this.getMapMarkers()}
                    {/*<MapMarker
                        lat={59.955413}
                        lng={30.337844}
                        text={'Kreyser Avrora'}
                    />*/}
                </GoogleMapReact>
            </div>
        );
    }
}

export default SimpleMap;
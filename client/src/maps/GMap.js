import React from 'react';
import compose from 'recompose/compose';
import defaultProps from 'recompose/defaultProps';
import GoogleMapReact from 'google-map-react';
import ClusterMarker from './markers/ClusterMarker';
import SimpleMarker from './markers/SimpleMarker';
import supercluster from 'points-cluster';
import {mapKey} from "./mapKey";

class gMap extends React.Component {


    setHoveredMarkerId = (id) => {
        this.setState({hoveredMarkerId: id});

    };

    setMapProps = ({center, zoom, bounds}) => {
        this.setState({mapProps: {center, zoom, bounds}});
    };
    onChange = ({center, zoom, bounds}) => {
        this.setMapProps({center, zoom, bounds});
    };

    onChildMouseEnter = (hoverKey, {id}) => {

        this.setHoveredMarkerId(id);
    };

    onChildMouseLeave = (/* hoverKey, childProps */) => {

        this.setHoveredMarkerId(-1);
    };

    componentDIdMount() {

    }

    componentDidUpdate(prevProps, prevState) {
        if (this.props.markers !== prevProps.markers) {
            const {markers = [], clusterRadius, options: {minZoom, maxZoom}} = this.props;
            this.setState({
                    getCluster: supercluster(
                        markers,
                        {
                            minZoom, // min zoom to generate clusters on
                            maxZoom, // max zoom level to cluster the points on
                            radius: clusterRadius, // cluster radius in pixels
                        }
                    )
                }
            )


        }
        if (this.state.mapProps !== prevState.mapProps && this.state.getCluster !== prevState.getCluster) {
            const {mapProps, getCluster} = this.state;
            this.setState({
                    clusters: mapProps.bounds
                        ? getCluster(mapProps)
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
        } = this.props;
        return (
            <GoogleMapReact
                options={options}
                hoverDistance={hoverDistance}
                center={center}
                zoom={zoom}
                onChange={this.onChange}
                onChildMouseEnter={this.onChildMouseEnter}
                onChildMouseLeave={this.onChildMouseLeave}
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
        )

    }
}

export const gMapHOC = compose(
    defaultProps({
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
    }),
);

export default gMapHOC(gMap);

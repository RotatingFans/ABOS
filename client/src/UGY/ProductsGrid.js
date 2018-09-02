import React, {Component} from 'react';
import ReactDataGrid from 'react-data-grid';
import {withStyles} from '@material-ui/core/styles';
import compose from 'recompose/compose';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import update from 'immutability-helper';

import {
    changeListParams,
    crudGetList,
    crudGetList as crudGetListAction,
    crudGetOne,
    crudUpdate,
    startUndoable
} from 'ra-core';
import {addField, fetchUtils, GET_LIST, GET_MANY, GET_ONE, Responsive, ViewTitle} from 'react-admin';
import restClient from '../grailsRestClient';
import CurrencyFormatter from "../resources/Formatters/CurrencyFormatter";
import ProductsToolbar from "./ProductsToolBar";

const {Editors, Formatters} = require('react-data-grid-addons');

const httpClient = (url, options = {}) => {
    if (!options.headers) {
        options.headers = new Headers({Accept: 'application/json'});
    }
    const token = localStorage.getItem('access_token');
    options.headers.set('Authorization', `Bearer ${token}`);
    return fetchUtils.fetchJson(url, options);
};
const dataProvider = restClient(httpClient);
//import dataProviderFactory from '../grailsRestClient';

const styles = theme => ({
    main: {
        fontFamily: theme.typography.fontFamily,
        fontSize: 14,
        lineHeight: '1.428571429',
        '& *, &:before, &:after': {
            boxSizing: 'border-box',
        },
        '& .widget-HeaderCell__value': {
            margin: 0,
            padding: 0,
        },
        '& .react-grid-HeaderCell__draggable': {
            margin: 0,
            padding: 0,
        },
        height: '100% !important'


    },
    'react-grid-Grid': {
        height: '100% !important'
    }


});

const emptyRow = {};


class ProductsGrid extends Component {

    state = {rows: [], order: {}, year: 0, userName: "", customer: {}, filter: {}};
    rowGetter = (i) => {
        return this.state.rows[i];
    };

    handleGridRowsUpdated = ({cellKey, fromRow, toRow, updated}) => {
        let rows = this.state.rows.slice();
        if (cellKey === "quantity") {

        }
        for (let i = fromRow; i <= toRow; i++) {
            let rowToUpdate = rows[i];

            let updatedRow = update(rowToUpdate, {$merge: updated});
            rows[i] = updatedRow;
        }
        this.setState({rows});
    };

    handleGridSort = (sortColumn, sortDirection) => {
        this.props.setSort(sortColumn, sortDirection);
    };
    updateDimensions = () => {
        let w = window,
            d = document,
            documentElement = d.documentElement,
            body = d.getElementsByTagName('body')[0],
            wrapperDiv = d.getElementById("dataGridWrapper"),
            width = w.innerWidth || documentElement.clientWidth || body.clientWidth,
            height = w.innerHeight || documentElement.clientHeight || body.clientHeight;
        // wrapperDiv.height = height + "px";
    };
    handleAddRow = ({newRowIndex}) => {
        const newRow = {
            humanProductId: newRowIndex,
            productName: '',
            unitSize: '',
            unitCost: ''
        };

        let parentState = update(this.state.rows, {
            $push: [newRow]
        });

        this.setState({rows: parentState});
    };
    getSize = () => {
        return this.state.rows.size;
    };
    handleFilterChange = (filter) => {
        let newFilters = Object.assign({}, this.state.filters);
        if (filter.filterTerm) {
            newFilters[filter.column.key] = filter;
        } else {
            delete newFilters[filter.column.key];
        }

        this.setState({filters: newFilters});
    };
    onClearFilters = () => {
        this.setState({filters: {}});
    };

    constructor(props) {
        super(props);
        this.perPageInitial = this.props.perPage;
        this.loading = false;
        this._columns = [
            {
                key: 'humanProductId',
                name: 'ID',
                editable: true,
                resizable: true,
                filterable: true,

            },
            {
                key: 'productName',
                name: 'Name',
                editable: true,
                resizable: true,
                filterable: true,
            },
            {
                key: 'unitSize',
                name: 'Size',
                editable: true,
                resizable: true,
                filterable: true,
            },
            {
                key: 'unitCost',
                name: 'Unit Cost',
                editable: true,
                formatter: CurrencyFormatter,
                resizable: true,
                filterable: true,
            }
        ];
    }

    componentDidMount() {
        const aMonthAgo = new Date();
        aMonthAgo.setDate(aMonthAgo.getDate() - 30);
        window.addEventListener("resize", this.updateDimensions);
        this.loadProducts();


    }

    loadProducts(year) {

        let filter = {};
        if (year) {
            filter = {year: year};

        }

        dataProvider(GET_LIST, 'Products', {
            filter: filter,
            pagination: {page: 1, perPage: 100},
            sort: {field: 'id', order: 'DESC'}
        })
            .then(response =>
                response.data.reduce((stats, product) => {
                        stats.products.push({
                            humanProductId: product.humanProductId,
                            id: product.id,
                            year: {id: product.year.id},
                            productName: product.productName,
                            unitSize: product.unitSize,
                            unitCost: product.unitCost,

                        });
                        return stats;
                    },
                    {
                        products: []
                        /*
                                                    humanProductId: '0',
                                                    id: 0,
                                                    year: {id: 0},
                                                    productName: '',
                                                    unitSize: '',
                                                    unitCost: 0.0,
                                                    quantity: 0,
                                                    extended_cost: 0.0,
                         */
                    }
                )
            ).then(({products}) => {
                this.setState({
                    rows: products
                });
                window.dispatchEvent(new Event('resize'));
            }
        );


    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.year !== this.props.year) {
            this.loadProducts(nextProps.year);

        }

    }

    componentWillUnmount() {

        window.removeEventListener("resize", this.updateDimensions);

    }

    render() {
        const {classes} = this.props;

        return (/*<div className="list-page List-root-156">
            <div className="MuiPaper-root-34 MuiPaper-elevation2-38 MuiPaper-rounded-35 MuiCard-root-160">*/
            <div className={classes.main}>
                {/*                <div id="dataGridWrapper" style={{position: "relative", height: "100%"}}>
                    <div style={{position: "absolute", width: "98%", height: "100%", margin: "1%"}}>*/}
                <ReactDataGrid
                    className="toto"
                    enableCellSelect={true}
                    columns={this._columns}
                    rowGetter={this.rowGetter}
                    rowsCount={this.state.rows.length}
                    onGridRowsUpdated={this.handleGridRowsUpdated}
                    minColumnWidth="30"
                    disabled={true}
                    toolbar={<ProductsToolbar onAddRow={this.handleAddRow} enableFilter={true}
                                              numberOfRows={this.getSize()}/>}
                    onAddFilter={this.handleFilterChange}
                    onClearFilters={this.onClearFilters}

                />
                {/*                    </div>
                </div>*/}

            </div>
            /*</div>
        </div>
            </div>
            </div>*/

        );
    }
}

ProductsGrid.propTypes = {
    label: PropTypes.string,

    className: PropTypes.string,

    year: PropTypes.number
};

ProductsGrid.defaultProps = {};
const mapStateToProps = (state, props) => ({});
const ProductsGridRaw = compose(
    withStyles(styles),
    connect(mapStateToProps, {
        changeListParams,
        dispatchCrudUpdate: crudUpdate,
        crudGetList: crudGetListAction,
        startUndoable,
    })
)(ProductsGrid);
export default (ProductsGridRaw); // decorate with redux-form's <Field>

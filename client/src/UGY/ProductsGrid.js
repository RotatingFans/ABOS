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
import MUITextEditor from "../resources/Editors/MUITextEditor";
import MUICurrencyEditor from "../resources/Editors/MUICurrencyEditor";
import MUISelectEditor from "../resources/Editors/MUISelectEditor";
import ProductsContextMenu from "./ProductsContextMenu";
import DropDownFormatter from '../resources/Formatters/DropDownFormatter';

//const {Editors, Formatters} = require('react-data-grid-addons');

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
        height: '100% !important',
        width: '100%'


    },
    dataGrid: {
        width: '100%'
    },
    'react-grid-Grid': {
        height: '100% !important'
    },
    contextMenu: {
        zIndex: 80000,
        backgroundColor: '#FFF'
    }


});

const emptyRow = {};


class ProductsGrid extends Component {

    state = {
        rows: [], order: {}, year: 0, userName: "", customer: {}, filter: {}, columns: [{
            key: 'humanProductId',
            name: 'ID',
            editable: true,
            resizable: true,
            filterable: true,
            editor: MUITextEditor

        },
            {
                key: 'productName',
                name: 'Name',
                editable: true,
                resizable: true,
                filterable: true,
                editor: MUITextEditor

            },
            {
                key: 'unitSize',
                name: 'Size',
                editable: true,
                resizable: true,
                filterable: true,
                editor: MUITextEditor

            },
            {
                key: 'unitCost',
                name: 'Unit Cost',
                editable: true,
                formatter: CurrencyFormatter,
                resizable: true,
                filterable: true,
                editor: MUICurrencyEditor

            },
            {
                key: 'category',
                name: 'Category',
                editable: true,
                resizable: true,
                filterable: true,
                editor: <MUISelectEditor options={[{id: '-1', value: ""}]}/>,
                formatter: <DropDownFormatter options={[{id: '-1', value: ""}]} value={"-1"}/>


            }]
    };
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
    handleAddRow = ({newRowIndex, newRow}) => {


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
    deleteRow = (e, {rowIdx}) => {
        this.state.rows.splice(rowIdx, 1);
        this.setState({rows: this.state.rows});
    };
    insertRowAbove = (e, {rowIdx}) => {
        this.insertRow(rowIdx);
    };

    componentDidMount() {
        const aMonthAgo = new Date();
        aMonthAgo.setDate(aMonthAgo.getDate() - 30);
        window.addEventListener("resize", this.updateDimensions);
        this.loadProducts(this.props.year);


    }

    insertRowBelow = (e, {rowIdx}) => {
        this.insertRow(rowIdx + 1);
    };

    componentWillReceiveProps(nextProps) {
        if (nextProps.year !== this.props.year) {
            this.loadProducts(nextProps.year);

        }

    }

    componentWillUnmount() {

        window.removeEventListener("resize", this.updateDimensions);

    }

    insertRow = (rowIdx) => {
        const newRow = {
            id: -2,
            humanProductId: '',
            productName: '',
            unitSize: '',
            unitCost: '0.00',
            category: '-1'
        };

        let rows = [...this.state.rows];
        rows.splice(rowIdx, 0, newRow);

        this.setState({rows});
    };

    constructor(props) {
        super(props);
        this.perPageInitial = this.props.perPage;
        this.loading = false;
        // this.createColumns();
    }

    createColumns(categories) {
        return [
            {
                key: 'humanProductId',
                name: 'ID',
                editable: true,
                resizable: true,
                filterable: true,
                editor: MUITextEditor

            },
            {
                key: 'productName',
                name: 'Name',
                editable: true,
                resizable: true,
                filterable: true,
                editor: MUITextEditor

            },
            {
                key: 'unitSize',
                name: 'Size',
                editable: true,
                resizable: true,
                filterable: true,
                editor: MUITextEditor

            },
            {
                key: 'unitCost',
                name: 'Unit Cost',
                editable: true,
                formatter: CurrencyFormatter,
                resizable: true,
                filterable: true,
                editor: MUICurrencyEditor

            },
            {
                key: 'category',
                name: 'Category',
                editable: true,
                resizable: true,
                filterable: true,
                editor: <MUISelectEditor options={categories}/>,
                formatter: <DropDownFormatter options={categories} value={"-1"}/>

            }
        ];
    }

    loadProducts(year) {

        let filter = {};
        if (year) {
            filter = {year: year};

        }
        dataProvider(GET_LIST, 'Categories', {
            filter: filter,
            pagination: {page: 1, perPage: 100},
            sort: {field: 'id', order: 'DESC'}
        })
            .then(response =>
                response.data.reduce((stats, category) => {
                        stats.categories.push({

                            id: category.id,
                            name: category.categoryName,
                            value: category.categoryName,


                        });

                        return stats;
                    },
                    {
                        categories: [],
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
            ).then(({categories}) => {
                categories.push({id: '-1', value: " "});

                this.setState({
                    categories: categories,
                    columns: this.createColumns(categories),

                });
                dataProvider(GET_LIST, 'Products', {
                    filter: filter,
                    pagination: {page: 1, perPage: 100},
                    sort: {field: 'id', order: 'DESC'}
                })
                    .then(response =>
                        response.data.reduce((stats, product) => {
                                let cat = -1;
                                if (product.category) {
                                    cat = product.category.id;
                                }
                                stats.products.push({
                                    humanProductId: product.humanProductId,
                                    id: product.id,
                                    year: {id: product.year.id},
                                    productName: product.productName,
                                    unitSize: product.unitSize,
                                    unitCost: product.unitCost,
                                    category: cat

                                });

                                return stats;
                            },
                            {
                                products: [],
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
        );



    }

    render() {
        const {classes} = this.props;

        return (/*<div className="list-page List-root-156">
            <div className="MuiPaper-root-34 MuiPaper-elevation2-38 MuiPaper-rounded-35 MuiCard-root-160">*/
            <div className={classes.main}>
                {/*                <div id="dataGridWrapper" style={{position: "relative", height: "100%"}}>
                    <div style={{position: "absolute", width: "98%", height: "100%", margin: "1%"}}>*/}
                <ReactDataGrid
                    className={classes.dataGrid}
                    enableCellSelect={true}
                    columns={this.state.columns}
                    rowGetter={this.rowGetter}
                    rowsCount={this.state.rows.length}
                    onGridRowsUpdated={this.handleGridRowsUpdated}
                    minColumnWidth="30"
                    // midWidth={"100px"}
                    toolbar={<ProductsToolbar onAddRow={this.handleAddRow} enableFilter={true}
                                              numberOfRows={this.getSize()}
                                              onImportExport={this.props.onImportExport}
                                              categories={this.state.categories}/>}
                    onAddFilter={this.handleFilterChange}
                    onClearFilters={this.onClearFilters}
                    contextMenu={<ProductsContextMenu className={classes.contextMenu} id="customizedContextMenu"
                                                      onRowDelete={this.deleteRow}
                                                      onRowInsertAbove={this.insertRowAbove}
                                                      onRowInsertBelow={this.insertRowBelow}/>}
                    columnEquality={() => false}

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
    onImportExport: PropTypes.func,
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

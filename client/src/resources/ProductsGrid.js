import React, {Component} from 'react';
import ReactDataGrid from 'react-data-grid';
import {withStyles} from '@material-ui/core/styles';
import compose from 'recompose/compose';
import {connect} from 'react-redux';

import {
    changeListParams,
    crudGetList,
    crudGetList as crudGetListAction,
    crudGetOne,
    crudUpdate,
    startUndoable
} from 'ra-core';
import {fetchUtils, GET_LIST, GET_MANY, GET_ONE, Responsive, ViewTitle} from 'react-admin';
import restClient from '../grailsRestClient';
import NumberEditor from "./Editors/NumberEditor";
import CurrencyFormatter from "./Formatters/CurrencyFormatter";

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

    },


});

const emptyRow = {};

class ProductsGrid extends Component {
    static defaultProps = {
        columns: [],
        data: {},
        hasBulkActions: false,
        ids: [],
        selectedIds: [],
        pageSize: 25,
    };
    state = {rows: []};
    rowGetter = (i) => {
        return this.state.rows[i];
    };
    handleGridRowsUpdated = ({cellKey, fromRow, toRow, updated}) => {
        let rows = this.state.rows.slice();
        if (cellKey === "quantity") {

        }
        for (let i = fromRow; i <= toRow; i++) {
            let rowToUpdate = rows[i];
            rowToUpdate.extended_cost = updated.quantity * rowToUpdate.unitCost;
            rowToUpdate.quantity = updated.quantity;
            //let updatedRow = update(rowToUpdate, {$merge: updated});
            rows[i] = rowToUpdate;
        }

        this.setState({rows});
    };
    handleGridSort = (sortColumn, sortDirection) => {
        this.props.setSort(sortColumn, sortDirection);
    };
    updateDimensions = () => {
        var w = window,
            d = document,
            documentElement = d.documentElement,
            body = d.getElementsByTagName('body')[0],
            wrapperDiv = d.getElementById("dataGridWrapper"),
            width = w.innerWidth || documentElement.clientWidth || body.clientWidth,
            height = w.innerHeight || documentElement.clientHeight || body.clientHeight;
        wrapperDiv.height = height + "px";
    };
    constructor(props) {
        super(props);
        this.perPageInitial = this.props.perPage;
        this.loading = false;
        this._columns = [
            {
                key: 'humanProductId',
                name: 'ID',
                resizable: true
            },
            {
                key: 'productName',
                name: 'Name',
                editable: false,
                resizable: true
            },
            {
                key: 'unitSize',
                name: 'Size',
                editable: false,
                resizable: true
            },
            {
                key: 'unitCost',
                name: 'Unit Cost',
                editable: false,
                formatter: CurrencyFormatter,
                resizable: true
            },
            {
                key: 'quantity',
                name: 'Quantity',
                editable: false,
                editor: NumberEditor,
                resizable: true
            },
            {
                key: 'extended_cost',
                name: 'Extended Cost',
                editable: false,
                formatter: CurrencyFormatter,
                resizable: true

            }
        ];
    }

    componentDidMount() {
        const aMonthAgo = new Date();
        aMonthAgo.setDate(aMonthAgo.getDate() - 30);
        window.addEventListener("resize", this.updateDimensions);
        const {record} = this.props;
        this.props.crudGetList(
            this.props.resource,
            {page: 1, perPage: 100},
            {field: 'id', order: 'DESC'},
            {}
        );
        let filter = {};
        if (record.year) {
            filter = {year: record.year.id};
        }
        if (record.order) {
            dataProvider(GET_ONE, 'Orders', {
                id: record.order.id
            })
                .then(orderResponse => {
                    dataProvider(GET_LIST, 'Products', {
                        filter: filter,
                        pagination: {page: 1, perPage: 100},
                        sort: {field: 'id', order: 'DESC'}
                    })
                        .then(response =>
                            response.data.reduce((stats, product) => {
                                    let match = orderResponse.data.orderedProducts.filter(order => {
                                        return order.products.id == product.id;
                                    });
                                    if (match.length > 0) {
                                        stats.products.push({
                                            humanProductId: product.humanProductId,
                                            id: product.id,
                                            year: {id: product.year.id},
                                            productName: product.productName,
                                            unitSize: product.unitSize,
                                            unitCost: product.unitCost,
                                            quantity: match[0].quantity,
                                            extended_cost: match[0].extendedCost
                                        });
                                    }
                                    else {
                                        stats.products.push({
                                            humanProductId: product.humanProductId,
                                            id: product.id,
                                            year: {id: product.year.id},
                                            productName: product.productName,
                                            unitSize: product.unitSize,
                                            unitCost: product.unitCost,
                                            quantity: 0,
                                            extended_cost: 0.0
                                        });
                                    }

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
                            this.setState({rows: products});
                            window.dispatchEvent(new Event('resize'));
                        }
                    );
                })
        } else {
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
                                quantity: 0,
                                extended_cost: 0.0
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
                    this.setState({rows: products});
                    window.dispatchEvent(new Event('resize'));
                }
            );

        }


    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.ids.length !== this.props.ids.length) {
            this.loading = false;
        }
    }


    /*    rowGetter = index => {
            const { data, ids, perPage, pageSize, setPerPage } = this.props;
            if (data[ids[index]]) {
                return data[ids[index]];
            }
            // ReactDataGrid doesn't support lazy loading
            // https://github.com/adazzle/react-data-grid/issues/152
            // and React complains if render() causes side effects
            // so we use setImmediate()
    /!*        if (!this.loading) {
                setImmediate(() => {
                    setPerPage(perPage + pageSize);
                });
                this.loading = true;
            }*!/
            return emptyRow;
        };*/

    componentWillUnmount() {
        this.props.changeListParams(this.props.resource, {
            ...this.props.params,
            perPage: this.perPageInitial,
        });
        window.removeEventListener("resize", this.updateDimensions);

    }

    /*    handleGridRowsUpdated = ({ fromRow, toRow, updated }) => {
            const {
                ids,
                data,
                startUndoable,
                dispatchCrudUpdate,
                undoable = true,
            } = this.props;
            for (let i = fromRow; i <= toRow; i++) {
                const id = ids[i];
                let rowToUpdate = data[id];
                if (undoable) {
                    startUndoable(
                        crudUpdate(
                            this.props.resource,
                            id,
                            updated,
                            rowToUpdate,
                            '',
                            false
                        )
                    );
                } else {
                    dispatchCrudUpdate(
                        this.props.resource,
                        id,
                        updated,
                        rowToUpdate,
                        '',
                        false
                    );
                }
            }
        };*/

    render() {
        const {classes, columns, currentSort, total} = this.props;
        const {orders} = this.state;

        return (/*<div className="list-page List-root-156">
            <div className="MuiPaper-root-34 MuiPaper-elevation2-38 MuiPaper-rounded-35 MuiCard-root-160">*/
            <div className={classes.main}>
                <div id="dataGridWrapper" style={{position: "relative", height: "660px"}}>
                    <div style={{position: "absolute", width: "98%", height: "100%", margin: "1%"}}>
                        <ReactDataGrid
                            className="toto"
                            enableCellSelect={true}
                            columns={this._columns}
                            rowGetter={this.rowGetter}
                            rowsCount={this.state.rows.length}
                            onGridRowsUpdated={this.handleGridRowsUpdated}
                            minColumnWidth="30"
                            minHeight="600px"
                        />
                    </div>
                </div>

            </div>
            /*</div>
        </div>
            </div>
            </div>*/

        );
    }
}

const mapStateToProps = (state, props) => ({
    params: state.admin.resources[props.resource].list.params,
});
export default compose(
    withStyles(styles),
    connect(mapStateToProps, {
        changeListParams,
        dispatchCrudUpdate: crudUpdate,
        crudGetList: crudGetListAction,

        startUndoable,
    })
)(ProductsGrid);
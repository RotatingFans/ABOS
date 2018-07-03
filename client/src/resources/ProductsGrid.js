import React, {Component} from 'react';
import ReactDataGrid from 'react-data-grid';
import {withStyles} from '@material-ui/core/styles';
import compose from 'recompose/compose';
import {connect} from 'react-redux';

import {changeListParams, crudGetOne, crudUpdate, startUndoable,} from 'ra-core';
import {fetchUtils, GET_LIST, GET_MANY, Responsive, ViewTitle} from 'react-admin';
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

    constructor(props) {
        super(props);
        this.perPageInitial = this.props.perPage;
        this.loading = false;
        this._columns = [
            {
                key: 'humanProductId',
                name: 'ID',
                width: 80
            },
            {
                key: 'productName',
                name: 'Name',
                editable: true
            },
            {
                key: 'unitSize',
                name: 'Size',
                editable: true
            },
            {
                key: 'unitCost',
                name: 'Unit Cost',
                editable: true,
                formatter: CurrencyFormatter
            },
            {
                key: 'quantity',
                name: 'Quantity',
                editable: true,
                editor: NumberEditor
            },
            {
                key: 'extended_cost',
                name: 'Extended Cost',
                editable: true,
                formatter: CurrencyFormatter

            }
        ];
    }

    componentDidMount() {
        const aMonthAgo = new Date();
        aMonthAgo.setDate(aMonthAgo.getDate() - 30);


        dataProvider(GET_LIST, 'Products', {
            filter: {},
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

        });


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

        return (
            <div className={classes.main}>
                <ReactDataGrid
                    className="toto"
                    enableCellSelect={true}
                    columns={this._columns}
                    rowGetter={this.rowGetter}
                    rowsCount={this.state.rows.length}
                    minHeight={500}
                    onGridRowsUpdated={this.handleGridRowsUpdated}
                />
            </div>
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
        startUndoable,
    })
)(ProductsGrid);
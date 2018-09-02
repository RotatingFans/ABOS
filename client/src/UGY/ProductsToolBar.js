import React from 'react';
import {withStyles} from '@material-ui/core/styles';

import PropTypes from 'prop-types';

const styles = theme => ({});


class ProductsToolbar extends React.Component {
    static propTypes = {
        onAddRow: PropTypes.func,
        onToggleFilter: PropTypes.func,
        enableFilter: PropTypes.bool,
        numberOfRows: PropTypes.number,
        addRowButtonText: PropTypes.string,
        filterRowsButtonText: PropTypes.string,
        children: PropTypes.any
    };

    static defaultProps = {
        enableAddRow: true,
        addRowButtonText: 'Add Row',
        filterRowsButtonText: 'Filter Rows'
    };

    onAddRow = () => {
        if (this.props.onAddRow !== null && this.props.onAddRow instanceof Function) {
            this.props.onAddRow({newRowIndex: this.props.numberOfRows});
        }
    };

    renderAddRowButton = () => {
        if (this.props.onAddRow) {
            return (<button type="button" className="btn" onClick={this.onAddRow}>
                {this.props.addRowButtonText}
            </button>);
        }
    };

    renderToggleFilterButton = () => {
        if (this.props.enableFilter) {
            return (<button type="button" className="btn" onClick={this.props.onToggleFilter}>
                {this.props.filterRowsButtonText}
            </button>);
        }
    };

    render() {
        return (
            <div className="react-grid-Toolbar">
                <div className="tools">
                    {this.renderAddRowButton()}
                    {this.renderToggleFilterButton()}
                    {this.props.children}
                </div>
            </div>);
    }
}

export default withStyles(styles, {withTheme: true})(ProductsToolbar);

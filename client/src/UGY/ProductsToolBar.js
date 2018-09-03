import React from 'react';
import {withStyles} from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import AddIcon from '@material-ui/icons/Add';
import FilterListIcon from '@material-ui/icons/FilterList';
import ImportExportIcon from '@material-ui/icons/ImportExport';
import IconButton from '@material-ui/core/IconButton';
import PropTypes from 'prop-types';
import TextField from '@material-ui/core/TextField'
import NumberFormat from 'react-number-format';

const styles = theme => ({
    button: {
        margin: theme.spacing.unit,
    },
    tools: {

        display: 'flex',
        flexDirection: 'row',
        flexShrink: 1,
        flexGrow: 0,
        justifyContent: 'flex-end',
        alignItems: 'center',

    },
    toolBar: {

        display: 'flex',
        flexDirection: 'row',
        flexShrink: 1,
        flexGrow: 1,
        alignItems: 'center',

    },
    additionFields: {
        display: 'flex',
        flexDirection: 'row',
        flexShrink: 1,
        flexGrow: 1,
    },
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        width: 200,
        flexGrow: 1,
    },

});

function NumberFormatCustom(props) {
    const {inputRef, onChange, ...other} = props;

    return (
        <NumberFormat
            {...other}
            getInputRef={inputRef}
            onValueChange={values => {
                onChange({
                    target: {
                        value: values.value,
                    },
                });
            }}
            thousandSeparator
            prefix="$"
            decimalScale={2}
            fixedDecimalScale={true}
            allowNegative={false}
        />
    );
}

NumberFormatCustom.propTypes = {
    inputRef: PropTypes.func.isRequired,
    onChange: PropTypes.func.isRequired,
};

class ProductsToolbar extends React.Component {
    static propTypes = {
        onAddRow: PropTypes.func,
        onImportExport: PropTypes.func,
        onToggleFilter: PropTypes.func,
        enableFilter: PropTypes.bool,
        numberOfRows: PropTypes.number,
        addRowButtonText: PropTypes.string,
        filterRowsButtonText: PropTypes.string,
        children: PropTypes.any
    };
    state = {
        HID: '',
        name: '',
        size: '',
        cost: '0.00',

    };
    static defaultProps = {
        enableAddRow: true,
        addRowButtonText: 'Add Row',
        filterRowsButtonText: 'Filter Rows'
    };

    onAddRow = () => {
        if (this.props.onAddRow !== null && this.props.onAddRow instanceof Function) {
            this.props.onAddRow({
                newRowIndex: this.props.numberOfRows, newRow: {
                    humanProductId: this.state.HID,
                    productName: this.state.name,
                    unitSize: this.state.size,
                    unitCost: this.state.cost
                }
            });
        }
        this.setState({
            HID: '',
            name: '',
            size: '',
            cost: '0.00',
        })
    };

    renderAddRowButton = () => {
        if (this.props.onAddRow) {
            return (<IconButton className={this.props.classes.button} aria-label="Add Rows" onClick={this.onAddRow}>
                <AddIcon/>
            </IconButton>);
        }
    };

    renderToggleFilterButton = () => {
        if (this.props.enableFilter) {
            return (<IconButton className={this.props.classes.button} aria-label="Filter"
                                onClick={this.props.onToggleFilter}>
                <FilterListIcon/>
            </IconButton>);
        }
    };
    renderImportExport = () => {
        if (this.props.onImportExport) {
            return (<Button variant={"contained"} className={this.props.classes.button} aria-label="Filter"
                            onClick={this.props.onImportExport}>
                Import/Export
                <ImportExportIcon/>
            </Button>);
        }
    };
    handleChange = name => event => {
        this.setState({
            [name]: event.target.value,
        });
    };
    render() {
        const {classes} = this.props;
        return (
            <div className={classes.toolBar}>
                <div className={classes.additionFields}>
                    <TextField
                        id="HID"
                        label="Product ID"
                        className={classes.textField}
                        value={this.state.HID}
                        onChange={this.handleChange('HID')}
                        margin="normal"
                    />
                    <TextField
                        id="name"
                        label="Product Name"
                        className={classes.textField}
                        value={this.state.name}
                        onChange={this.handleChange('name')}
                        margin="normal"
                    />
                    <TextField
                        id="size"
                        label="Product size"
                        className={classes.textField}
                        value={this.state.size}
                        onChange={this.handleChange('size')}
                        margin="normal"
                    />
                    <TextField
                        id="cost"
                        label="Product cost"

                        className={classes.textField}
                        value={this.state.cost}
                        onChange={this.handleChange('cost')}
                        margin="normal"
                        InputProps={{
                            inputComponent: NumberFormatCustom,
                        }}
                    />
                </div>
                <div className={classes.tools}>
                    {this.renderAddRowButton()}
                    {this.renderToggleFilterButton()}
                    {this.renderImportExport()}

                    {this.props.children}
                </div>
            </div>
        );
    }
}

export default withStyles(styles, {withTheme: true})(ProductsToolbar);

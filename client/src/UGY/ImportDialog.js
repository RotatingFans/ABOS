import React from "react";
import Wizard from "../Reports/Wizard";
import {push} from 'react-router-redux';
import {connect} from 'react-redux';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import PropTypes from 'prop-types';
import {withStyles} from '@material-ui/core/styles';
/*import Select from '@material-ui/core/Select';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import TextField from '@material-ui/core/TextField';*/

import {
    BooleanInput,
    CREATE,
    fetchUtils,
    FileField,
    FileInput,
    FormDataConsumer,
    GET_LIST,
    ImageField,
    ImageInput,
    ReferenceArrayInput,
    ReferenceInput,
    required,
    SelectArrayInput,
    SelectInput,
    showNotification,
    SimpleForm,
    TextInput
} from 'react-admin';

const importSteps = () => [
    "Import Type", "File Selection"
];
const requiredValidate = required();
const CustomSelectInput = ({onChangeCustomHandler, ...rest}) => (
    <SelectInput onChange={(event, key, payload) => {
        onChangeCustomHandler(key)
    }}
                 {...rest}
    />
);
const formValidate = required();
const styles = theme => ({});

class ImportDialog extends React.Component {
    state = {
        action: '',
        importType: ''
    };

    updateAction = (value) => {
        this.setState({action: value})
    };

    setImportType = type => {
        this.setState({importType: type})

    };
    import = () => {

    };
    showStep = (step) => {
        return true;
    };

    stepsContent() {

        this.setState({
                importStepsContent: [

                    [
                        <CustomSelectInput
                            source="action" choices={[{id: 'CSV', name: 'Import From CSV'}, {
                            id: 'XML',
                            name: 'Import From XML'
                        }]} validate={requiredValidate} onChangeCustomHandler={(key) => this.setImportType(key)}/>

                    ], <FileInput source="file" label="Import File" accept="application/pdf">
                        <FileField source="src" title="title"/>
                    </FileInput>

                ]
            }
        )
    }

    componentWillMount() {
        this.stepsContent();

    }

    render() {
        return (
            <Dialog
                key={"importDialog"}
                open={this.props.importDialogOpen}
                onClose={this.props.closeImportDialog}
                aria-labelledby="form-dialog-title"
            >
                <DialogTitle id="form-dialog-title">Import</DialogTitle>
                <DialogContent>
                    <DialogContentText>

                    </DialogContentText>
                    <Wizard {...this.props} steps={importSteps()} stepContents={this.state.importStepsContent}
                            save={this.import}
                            formName={"record-form"}/>
                </DialogContent>

            </Dialog>
        )
    }
}

ImportDialog.propTypes = {
    closeImportDialog: PropTypes.func.required,
    importDialogOpen: PropTypes.bool.required

};

export default connect(null, {
    push,
    showNotification,

})(withStyles(styles, {withTheme: true})(ImportDialog));
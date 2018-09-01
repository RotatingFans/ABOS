import React from "react";

import PropTypes from 'prop-types';
import {withStyles} from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import Typography from '@material-ui/core/Typography';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';

import {
    BooleanInput,
    fetchUtils,
    FormDataConsumer,
    GET_LIST,
    ImageField,
    ImageInput,
    ReferenceArrayInput,
    ReferenceInput,
    required,
    SelectArrayInput,
    SelectInput,
    SimpleForm,
    TextInput
} from 'react-admin';

const drawerWidth = 240;


const styles = theme => ({
    root: {
        flexGrow: 1,
        zIndex: 1,
        overflow: 'hidden',
        position: 'relative',
        display: 'flex',
        width: '100%',
        height: '100%'
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
    },
    userPanel: {
        width: '100%',
    },
    modal: {
        top: '10%',
        left: '10%',
        width: '80%',
        height: '80%',
        position: 'absolute',
    },
    appBar: {
        position: 'absolute',

        zIndex: theme.zIndex.drawer + 1,

    },
    navIconHide: {
        [theme.breakpoints.up('md')]: {
            display: 'none',
        },
    },
    toolbar: theme.mixins.toolbar,
    drawerPaper: {
        width: drawerWidth,
        [theme.breakpoints.up('md')]: {
            position: 'relative',
        },
    },
    content: {
        flexGrow: 1,
        backgroundColor: theme.palette.background.default,
        padding: theme.spacing.unit,
        transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
    },
    'content-left': {
        marginLeft: -drawerWidth,
    },
    'content-right': {
        marginRight: -drawerWidth,
    },
    contentShift: {
        transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    'contentShift-left': {
        marginLeft: 0,
    },
    'contentShift-right': {
        marginRight: 0,
    },
    flex: {
        flexGrow: 1,
    },
    flexCenter: {
        flexGrow: 1,

        display: 'flex',

        alignItems: 'center',
    },
    topLevelExpansionPanel: {
        display: 'block',
    },
    'tabScroll': {
        height: '85%',
        overflow: 'scroll',
    },
});

class UserListItem extends React.PureComponent {
    state = {
        checked: false
    };
    setChecked = event => {
        const {userName, user, handleManageCheckBoxChange} = this.props;
        this.setState({checked: event.target.checked});

        handleManageCheckBoxChange(userName, user)(event);
    };

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.setState({checked: this.props.checked});

    }

    render() {
        const {user} = this.props;
        return (
            <ListItem button>
                <Checkbox
                    checked={this.state.checked}
                    onChange={this.setChecked}
                    value={user}
                />
                <ListItemText primary={user}/>
            </ListItem>
        );
    }
}

UserListItem.propTypes = {
    user: PropTypes.string.isRequired,
    userName: PropTypes.string.isRequired,
    handleManageCheckBoxChange: PropTypes.func.isRequired,
    checked: PropTypes.bool.isRequired,
};
const stopPropagation = (e) => e.stopPropagation();
const InputWrapper = ({children}) =>
    <div onClick={stopPropagation} style={{display: 'inline-flex'}}>
        {children}
    </div>;
class UserPanel extends React.PureComponent {
    state = {
        checked: false,
        expanded: false,
        checkboxClicked: false,
    };
    renderUserManagementList = userName => {
        const {userChecks, handleManageCheckBoxChange} = this.props;
        let listItems = [];
        Object.keys(userChecks[userName].subUsers).forEach((keyVal) => {
            let user = keyVal;
            let checked = userChecks[userName].subUsers[user];
            listItems.push(<UserListItem key={userName + "-sub-" + user} userName={userName}
                                         handleManageCheckBoxChange={handleManageCheckBoxChange} user={user}
                                         checked={checked}/>)


        });
        return (<List>
            {listItems}


        </List>)
    };

    setChecked = event => {
        const {userName, handleCheckBoxChange} = this.props;
        this.setState({checked: event.target.checked, checkboxClicked: true});

        handleCheckBoxChange(userName)(event);
    };
    handleUserPanelExpanded = (event, expanded) => {
        // if (!this.state.checkboxClicked) {
        this.setState({'expanded': expanded, checkboxClicked: false})
        //}
    };

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.setState({checked: this.props.checked});

    }


    // usage:

    render() {
        const {userName, classes} = this.props;
        return (<ExpansionPanel className={classes.userPanel} expanded={this.state.expanded}
                                onChange={this.handleUserPanelExpanded}>
                <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                    <InputWrapper>
                        <Checkbox
                            checked={this.state.checked}
                            onChange={this.setChecked}
                            value={userName}
                        />
                    </InputWrapper>

                    <div className={classes.flexCenter}>

                        <Typography className={classes.heading}>{userName}</Typography>
                    </div>
                    <div>
                        <InputWrapper>

                            <Button variant="contained" className={classes.button}>
                                Edit
                            </Button>
                        </InputWrapper>
                        <InputWrapper>

                            <Button variant="contained" color={'red'} className={classes.button}>
                                Delete
                            </Button>
                        </InputWrapper>

                    </div>
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    {this.renderUserManagementList(userName)}
                </ExpansionPanelDetails>
            </ExpansionPanel>
        )

    }


}

UserPanel.propTypes = {
    classes: PropTypes.object.isRequired,
    theme: PropTypes.object.isRequired,
    userName: PropTypes.string.isRequired,
    userChecks: PropTypes.object.isRequired,
    handleManageCheckBoxChange: PropTypes.func.isRequired,
    handleCheckBoxChange: PropTypes.func.isRequired,
    checked: PropTypes.bool.isRequired,
};
export default withStyles(styles, {withTheme: true})(UserPanel);

import React from 'react';

import PropTypes from 'prop-types';
import {withStyles} from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import List from '@material-ui/core/List';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import Hidden from '@material-ui/core/Hidden';
import Divider from '@material-ui/core/Divider';
import MenuIcon from '@material-ui/icons/Menu';
import AddIcon from '@material-ui/icons/Add';
import MoreVert from '@material-ui/icons/MoreVert';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Modal from '@material-ui/core/Modal';
import classNames from 'classnames';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
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

import restClient from "../grailsRestClient";


const drawerWidth = 240;


const httpClient = (url, options = {}) => {
    if (!options.headers) {
        options.headers = new Headers({Accept: 'application/json'});
    }
    const token = localStorage.getItem('access_token');
    options.headers.set('Authorization', `Bearer ${token}`);
    return fetchUtils.fetchJson(url, options);
};
const dataProvider = restClient(httpClient);

function TabContainer(props) {
    return (
        <Typography component="div">
            {props.children}
        </Typography>
    );
}

TabContainer.propTypes = {
    children: PropTypes.node.isRequired,
};


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
});



class UGYEditor extends React.Component {
    //users: {}, years: {}, customers: {}
    state = {
        value: 0,
        yearNavOpen: true,
        anchor: 'left',
        update: false,
        userBulkMenuAnchor: null,
        userAddMenuAnchor: null,
        user1: false,
    };

    handleDrawerToggle = () => {
        this.setState(state => ({yearNavOpen: !state.yearNavOpen}));
    };

    save = (record, redirect) => {
        console.log(record);
        let options = {};
        let url = 'http://localhost:8080/api/Reports';
        if (!options.headers) {
            options.headers = new Headers({Accept: 'application/pdf'});
        }
        const token = localStorage.getItem('access_token');
        options.headers.set('Authorization', `Bearer ${token}`);

        fetch(url, {
            method: "POST",
            mode: "cors",
            cache: "no-cache",
            credentials: "same-origin", // include, same-origin, *omit
            headers: {
                "Content-Type": "application/json; charset=utf-8",
                'Authorization': `Bearer ${token}`
                // "Content-Type": "application/x-www-form-urlencoded",
            },
            redirect: "follow", // manual, *follow, error
            referrer: "no-referrer", // no-referrer, *client
            body: JSON.stringify(record),
        }).then(response => {

        })


        //console.log(fetchUtils.fetchJson(url, options));

    };

    constructor(props) {
        super(props);

    }

    getUsers() {
        dataProvider(GET_LIST, 'User', {
            filter: {},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            this.setState({users: response.data})
        })


    }

    getYears() {
        dataProvider(GET_LIST, 'Years', {
            filter: {},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            this.setState({years: response.data})
        })


    }

    updateYear(year) {
        this.setState({year: year, update: true});
        // this.updateChoices();

    }

    updateUser(user) {
        this.setState({user: user, update: true});
        // this.updateChoices();
    }


    componentWillReceiveProps() {
        this.getUsers();
        this.getYears();

    }

    componentWillMount() {

    }

    handleChangeAnchor = event => {
        this.setState({
            anchor: event.target.value,
        });
    };
    handleUserBulkMenu = event => {
        this.setState({userBulkMenuAnchor: event.currentTarget});
    };

    handleUserBulkMenuClose = () => {
        this.setState({userBulkMenuAnchor: null});
    };
    handleUserAddMenu = event => {
        this.setState({userAddMenuAnchor: event.currentTarget});
    };

    handleUserAddMenuClose = () => {
        this.setState({userAddMenuAnchor: null});
    };
    handleChange = (event, value) => {
        this.setState({value});
    };
    handleCheckBoxChange = name => event => {
        this.setState({[name]: event.target.checked});
    };

    render() {
        const {classes, theme} = this.props;
        const {value, anchor, yearNavOpen, userBulkMenuAnchor, userAddMenuAnchor} = this.state;
        const userBulkMenuOpen = Boolean(userBulkMenuAnchor);
        const userAddMenuOpen = Boolean(userAddMenuAnchor);
        const drawer = (
            <div>
                <div className={classes.toolbar}/>
                <Divider/>
                <List>
                    <ListItem button>
                        <ListItemText primary="Trash"/>
                    </ListItem>
                </List>

            </div>
        );
        const usersTab = (
            <div>

                <Toolbar>
                    <Typography variant="title" color="inherit" className={classes.flex}>

                    </Typography>
                    <div>
                        <IconButton
                            aria-owns={userAddMenuOpen ? 'user-add-menu' : null}
                            aria-haspopup="true"
                            onClick={this.handleUserAddMenu}
                            color="inherit"
                        >
                            <AddIcon/>
                        </IconButton>
                        <Menu
                            id="user-add-menu"
                            anchorEl={userAddMenuAnchor}
                            anchorOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                            open={userAddMenuOpen}
                            onClose={this.handleUserAddMenuClose}
                        >
                            <MenuItem onClick={this.handleUserAddMenuClose}>Add Single User</MenuItem>
                            <MenuItem onClick={this.handleUserAddMenuClose}>Add Bulk Users</MenuItem>
                        </Menu>
                        <IconButton
                            aria-owns={userBulkMenuOpen ? 'user-Bulk-Menu' : null}
                            aria-haspopup="true"
                            onClick={this.handleUserBulkMenu}
                            color="inherit"
                        >
                            <MoreVert/>
                        </IconButton>
                        <Menu
                            id="user-Bulk-Menu"
                            anchorEl={userBulkMenuAnchor}
                            anchorOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                            open={userBulkMenuOpen}
                            onClose={this.handleUserBulkMenuClose}
                        >
                            <MenuItem onClick={this.handleUserBulkMenuClose}>Add Selected to Group</MenuItem>
                            <MenuItem onClick={this.handleUserBulkMenuClose}>Remove Selected from year</MenuItem>
                            <MenuItem onClick={this.handleUserBulkMenuClose}>Enable Selected</MenuItem>
                            <MenuItem onClick={this.handleUserBulkMenuClose}>Add selected to User</MenuItem>
                            <MenuItem onClick={this.handleUserBulkMenuClose}>Archive selected users</MenuItem>
                        </Menu>

                    </div>

                </Toolbar>
                <div>
                    <ExpansionPanel>
                        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                            <div className={classes.flex}>

                                <Typography className={classes.heading}>Enabled Users</Typography>
                            </div>

                        </ExpansionPanelSummary>
                        <ExpansionPanelDetails>
                            <ExpansionPanel className={classes.userPanel}>
                                <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                    <Checkbox
                                        checked={this.state.user1}
                                        onChange={this.handleCheckBoxChange('user1')}
                                        value="user1"
                                    />
                                    <div className={classes.flex}>

                                        <Typography className={classes.heading}>User 1</Typography>
                                    </div>
                                    <div>
                                        <Button variant="contained" className={classes.button}>
                                            Edit
                                        </Button>
                                        <Button variant="contained" color={'red'} className={classes.button}>
                                            Delete
                                        </Button>
                                    </div>
                                </ExpansionPanelSummary>
                                <ExpansionPanelDetails>
                                    <List>
                                        <ListItem button>
                                            <Checkbox
                                                checked={this.state.user1}
                                                onChange={this.handleCheckBoxChange('user1')}
                                                value="user1"
                                            />
                                            <ListItemText primary="Trash"/>
                                        </ListItem>
                                        <ListItem button>
                                            <Checkbox
                                                checked={this.state.user1}
                                                onChange={this.handleCheckBoxChange('user1')}
                                                value="user1"
                                            />
                                            <ListItemText primary="Trash"/>
                                        </ListItem>
                                    </List>
                                </ExpansionPanelDetails>
                            </ExpansionPanel>

                        </ExpansionPanelDetails>
                    </ExpansionPanel>
                </div>
            </div>
        );
        const groupsTab = (
            <div>
                Groups

            </div>
        );
        const prodsTab = (
            <div>
                Products

            </div>
        );
        /*
        *                         | Tab Pane
        *                         |    Users | Groups | Products
        *                         |
        *                         |     U Menu Bar - Add Element (Dropdown for bulk or simple) Multi Action Menu
        *                         |     S   Expansion Panels( Enabled, Disabled, Archived)
        *                         |     E     Selectable Expansion Panels with Delete/Edit buttons on end
        *                         |     R       Group Selection
        *                         |     S       Management Selection - Use Selectable Nested List
        *                         |
        *      Nested List        |     G Menu Bar - Add Element (Dropdown for bulk or simple) Multi Action Menu
        * See List on Material UI |     R   Expansion Panels with Delete/Edit buttons on end E
        *                         |     O     Edit Button Has option to remove all selected group members from group
        *                         |     U   List of Group Members(Selectable)
        *                         |     P
        *                         |
        *                         |     P Mimic Add Customer, but Some Changes
        *                         |     R   Top Pane
        *                         |     O     Different Import/Export function buttons
        *                         |     D   Add Product inputs/Button
        *                         |     U   Table
        *                         |     C   No Quantity/Extended Cost
        *                         |     T   Add Category Selection
        *                         |     S     Include Add Category Button - Should open a modal dialog
        *                         |
        *                         |------------------------------------------------------------------- Save | Cancel ---
         */
        return (
            <Modal

                open={true}
                disableBackdropClick={true}
            >
                <div className={classes.modal}>
                    <div className={classes.root}>
                        <AppBar className={classes.appBar}>
                            <Toolbar>
                                <IconButton
                                    color="inherit"
                                    aria-label="Open drawer"
                                    onClick={this.handleDrawerToggle}
                                >
                                    <MenuIcon/>
                                </IconButton>
                                <Typography variant="title" color="inherit" noWrap>
                                    Users, Groups, and Years
                                </Typography>
                            </Toolbar>
                        </AppBar>
                        <Hidden mdUp>
                            {/*<Drawer
                                variant="temporary"
                                anchor={theme.direction === 'rtl' ? 'right' : 'left'}
                                open={this.state.mobileOpen}
                                onClose={this.handleDrawerToggle}
                                classes={{
                                    paper: classes.drawerPaper,
                                }}
                                ModalProps={{
                                    keepMounted: true, // Better open performance on mobile.
                                }}
                            >
                                {drawer}
                            </Drawer>*/},
                            <Drawer
                                variant="permanent"
                                open={this.state.yearNavOpen}
                                onClose={this.handleDrawerToggle}
                                classes={{
                                    paper: classes.drawerPaper,
                                }}
                            >
                                {drawer}
                            </Drawer>
                        </Hidden>
                        <Hidden smDown implementation="css">
                            <Drawer
                                variant="persistent"
                                anchor={theme.direction === 'rtl' ? 'right' : 'left'}

                                open={this.state.yearNavOpen}
                                onClose={this.handleDrawerToggle}
                                classes={{
                                    paper: classes.drawerPaper,
                                }}
                            >
                                {drawer}
                            </Drawer>
                        </Hidden>
                        <main className={classNames(classes.content, classes[`content-${anchor}`], {
                            [classes.contentShift]: yearNavOpen,
                            [classes[`contentShift-${anchor}`]]: yearNavOpen,
                        })}>
                            <div className={classes.toolbar}/>

                            <Tabs value={value} onChange={this.handleChange}>
                                <Tab label="Users"/>
                                <Tab label="Groups"/>
                                <Tab label="Products"/>
                            </Tabs>
                            {value === 0 && <TabContainer>{usersTab}</TabContainer>}
                            {value === 1 && <TabContainer>{groupsTab}</TabContainer>}
                            {value === 2 && <TabContainer>{prodsTab}</TabContainer>}
                        </main>
                    </div>
                </div>
            </Modal>

        )
    }
}

UGYEditor.propTypes = {
    classes: PropTypes.object.isRequired,
    theme: PropTypes.object.isRequired,
};

export default withStyles(styles, {withTheme: true})(UGYEditor);


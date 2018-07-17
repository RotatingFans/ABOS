import React, {Component} from 'react';
import {withStyles} from '@material-ui/core/styles';
import compose from 'recompose/compose';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import Tabs from '@material-ui/core/Tabs';
import {addField} from 'react-admin';
import Card from '@material-ui/core/Card';
import Typography from '@material-ui/core/Typography';


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

function TabContainer(props) {
    return (
        <Typography component="div" style={{padding: 8 * 3}}>
            {props.children}
        </Typography>
    );
}

TabContainer.propTypes = {
    children: PropTypes.node.isRequired,
};

class Wizard extends Component {
    state = {
        value: 0,
    };

    handleChange = (event, value) => {
        this.setState({value});
    };

    updateDimensions = () => {

    };

    constructor(props) {
        super(props);
        this.loading = false;

    }

    componentDidMount() {


    }


    componentWillReceiveProps(nextProps) {


    }


    componentWillUnmount() {
        /*        this.props.changeListParams(this.props.resource, {
                    ...this.props.params,

                });
                window.removeEventListener("resize", this.updateDimensions);*/

    }


    render() {
        const {classes, tabs} = this.props;
        const {value} = this.state;
        //Tab Pane
        //Next Button/Prev Button
        //Loop through tabs supplied as children
        return (
            <Card>
                <div className={classes.main}>
                    <Tabs
                        children={tabs}
                        value={value} onChange={this.handleChange}
                    >
                    </Tabs>
                    {value === 0 && <TabContainer>Item One</TabContainer>}
                </div>

            </Card>


        );
    }
}

Wizard.propTypes = {
    label: PropTypes.string,
    options: PropTypes.object,
    source: PropTypes.string,
    input: PropTypes.object,
    className: PropTypes.string,
    tabs: PropTypes.node,

};

Wizard.defaultProps = {};

const WizardRaw = compose(
    withStyles(styles),
)(Wizard);
export default WizardRaw; // decorate with redux-form's <Field>

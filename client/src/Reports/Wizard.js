import React, {Component} from 'react';
import {withStyles} from '@material-ui/core/styles';
import compose from 'recompose/compose';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {addField} from 'react-admin';
import Card from '@material-ui/core/Card';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';


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
    root: {
        flexGrow: 1,
    },
    flex: {
        flexGrow: 1,
    },
    menuButton: {
        marginLeft: -12,
        marginRight: 20,
    },
    button: {
        marginRight: theme.spacing.unit,
    },
    instructions: {
        padding: 8 * 3,
    },


});

class HorizontalLinearStepper extends React.Component {
    state = {
        activeStep: 0,
        skipped: new Set(),
    };
    isStepOptional = step => {
        return false;
    };
    handleNext = () => {
        const {activeStep} = this.state;
        let {skipped} = this.state;
        if (this.isStepSkipped(activeStep)) {
            skipped = new Set(skipped.values());
            skipped.delete(activeStep);
        }
        this.setState({
            activeStep: activeStep + 1,
            skipped,
        });
    };
    handleBack = () => {
        const {activeStep} = this.state;
        this.setState({
            activeStep: activeStep - 1,
        });
    };
    handleSkip = () => {
        const {activeStep} = this.state;
        if (!this.isStepOptional(activeStep)) {
            // You probably want to guard against something like this,
            // it should never occur unless someone's actively trying to break something.
            throw new Error("You can't skip a step that isn't optional.");
        }

        this.setState(state => {
            const skipped = new Set(state.skipped.values());
            skipped.add(activeStep);
            return {
                activeStep: state.activeStep + 1,
                skipped,
            };
        });
    };
    handleReset = () => {
        this.setState({
            activeStep: 0,
        });
    };

    getSteps() {
        return this.props.steps;
    }

    getStepContent(step) {
        return (this.props.stepContents[step]);


    }

    isStepSkipped(step) {
        return this.state.skipped.has(step);
    }

    render() {
        const {classes} = this.props;
        const steps = this.getSteps();
        const {activeStep} = this.state;

        return (
            <div className={classes.root}>
                <Stepper activeStep={activeStep}>
                    {steps.map((label, index) => {
                        const props = {};
                        const labelProps = {};
                        if (this.isStepOptional(index)) {
                            labelProps.optional = <Typography variant="caption">Optional</Typography>;
                        }
                        if (this.isStepSkipped(index)) {
                            props.completed = false;
                        }
                        return (
                            <Step key={label} {...props}>
                                <StepLabel {...labelProps}>{label}</StepLabel>
                            </Step>
                        );
                    })}
                </Stepper>
                <div>
                    {activeStep === steps.length ? (
                        <div>
                            <Typography className={classes.instructions}>
                                All steps completed - you&quot;re finished
                            </Typography>
                            <Button onClick={this.handleReset} className={classes.button}>
                                Reset
                            </Button>
                        </div>
                    ) : (
                        <div>
                            <Typography component="div"
                                        className={classes.instructions}>{this.getStepContent(activeStep)}</Typography>
                            <div>
                                <Button
                                    disabled={activeStep === 0}
                                    onClick={this.handleBack}
                                    className={classes.button}
                                >
                                    Back
                                </Button>
                                {this.isStepOptional(activeStep) && (
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={this.handleSkip}
                                        className={classes.button}
                                    >
                                        Skip
                                    </Button>
                                )}
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={this.handleNext}
                                    className={classes.button}
                                >
                                    {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
                                </Button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        );
    }
}

HorizontalLinearStepper.propTypes = {
    classes: PropTypes.object,
    steps: PropTypes.arrayOf(PropTypes.string),
    stepContents: PropTypes.arrayOf(PropTypes.node)
};

class Wizard extends Component {


    render() {
        const {classes} = this.props;
        //Tab Pane
        //Next Button/Prev Button
        //Loop through tabs supplied as children
        return (
            <Card>
                <div className={classes.main}>
                    <HorizontalLinearStepper {...this.props}/>

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
    steps: PropTypes.arrayOf(PropTypes.string),
    stepContents: PropTypes.arrayOf(PropTypes.node)

};

Wizard.defaultProps = {};

const WizardRaw = compose(
    withStyles(styles),
)(Wizard);
export default WizardRaw; // decorate with redux-form's <Field>

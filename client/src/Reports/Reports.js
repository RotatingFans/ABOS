import React from 'react';

import Wizard from './Wizard'

const steps = () => [
    "World", "World2"
];
const stepsContent = () => [
    <div>Hello World</div>, <div>Hello World2</div>
];
export const Reports = (props) => (
    <Wizard {...props} steps={steps()} stepContents={stepsContent()}/>
);


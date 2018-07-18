import React from 'react';

import Wizard from './Wizard'
import Tab from '@material-ui/core/Tab';

const tabs = () => [
    <Tab label={"World"}>
        <div>Hello World</div>
    </Tab>,
    <Tab label={"Wor2ld"}>
        <div>Hello World2</div>
    </Tab>
];

export const Reports = (props) => (
    <Wizard {...props} tabs={tabs()}/>
);


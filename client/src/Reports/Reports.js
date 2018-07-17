import React from 'react';

import Wizard from './Wizard'
import Tab from '@material-ui/core/Tab';

const tabs = () => (
    <Tab label={"World"}>
        <div>Hello World</div>
    </Tab>
);

export const Reports = (props) => (
    <Wizard {...props} tabs={tabs()}/>
);


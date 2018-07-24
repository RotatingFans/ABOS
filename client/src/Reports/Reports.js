import React from 'react';
import {BooleanInput, fetchUtils, ImageField, ImageInput, SelectInput, SimpleForm, TextInput} from 'react-admin';
import Wizard from './Wizard'
import download from 'downloadjs';

const steps = () => [
    "Pick Report Template", "Fill In Details"
];


function save(record, redirect) {
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
    }).then(response => response.blob())
        .then(blob => download(blob, "report.pdf", "application/pdf"))

    //console.log(fetchUtils.fetchJson(url, options));

}

const stepsContent = () => [
    <SelectInput
        source="template" choices={[{id: 'customers_split', name: 'Year; Split by Customer'}]}/>,
    [
        <TextInput
            source="Scout_name"/>,
        <TextInput
            source="Scout_address"/>,
        <TextInput
            source="Scout_Zip"/>,
        <TextInput
            source="Scout_Town"/>,
        <TextInput
            source="Scout_State"/>,
        <TextInput
            source="Scout_Phone"/>,
        <TextInput
            source="Scout_Rank"/>,
        <ImageInput
            source="LogoLocation" accept="image/*">
            <ImageField source="src" title="title"/>
        </ImageInput>,
        <SelectInput
            source="Year" choices={[{id: 'customers_split', name: 'Year; Split by Customer'}]}/>,
        <SelectInput
            source="User" choices={[{id: 'customers_split', name: 'Year; Split by Customer'}]}/>,
        <SelectInput
            source="Customer" choices={[{id: 'customers_split', name: 'Year; Split by Customer'}]}/>,
        <SelectInput
            source="Category" choices={[{id: 'customers_split', name: 'Year; Split by Customer'}]}/>,
        <BooleanInput
            source="Print_Due_Header"/>,

    ]
];

export const Reports = (props) => (
    <Wizard {...props} steps={steps()} stepContents={stepsContent()} save={save} formName={"Record-form"}/>
);


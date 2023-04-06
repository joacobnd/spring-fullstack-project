import {
    WrapItem,
    Wrap,
    Text,
    Spinner
} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.jsx";
import CardWithImage from "./components/customer/Card.jsx";
import CreateCustomerDrawer from "./components/customer/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notifications.jsx";

const Home = () => {

    return (
        <SidebarWithHeader>
            <Text fontSize={"6xl"} > HOME </Text>
        </SidebarWithHeader>
    );
};

export default Home;

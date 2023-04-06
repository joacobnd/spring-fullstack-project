import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Input, useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

const AddIcon = () => "+";
const CreateCustomerDrawer = ({ fetchCustomers }) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return <>
        <Button
            lefticon={<AddIcon/>}
            colorScheme={"teal"}
            onClick={onOpen}>
            Create Customer
        </Button>

        <Drawer isOpen={isOpen} onClose={onClose} size={"sm"}>
            <DrawerOverlay/>
            <DrawerContent>
                <DrawerCloseButton/>
                <DrawerHeader>Create a new customer</DrawerHeader>

                <DrawerBody>
                    <CreateCustomerForm
                        onSuccess={fetchCustomers}
                    />
                </DrawerBody>

                <DrawerFooter>
                    <Button
                        lefticon={<AddIcon/>}
                        colorScheme={"teal"}
                        onClick={onClose}
                    >
                        Close
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    </>;

}

export default CreateCustomerDrawer;








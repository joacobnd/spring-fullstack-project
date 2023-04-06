import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    Tag,
    useColorModeValue,
    Button,
    useDisclosure,
    AlertDialog,
    AlertDialogOverlay,
    AlertDialogContent,
    AlertDialogHeader,
    AlertDialogBody, AlertDialogFooter,
} from '@chakra-ui/react';
import {useRef} from "react";
import {deleteCustomer} from "../../services/client.jsx";
import {errorNotification, successNotification} from "../../services/notifications.jsx";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";
import UpdateCustomerDrawer from "./UpdateCustomerDrawer.jsx";

export default function CardWithImage({id, name, email, age, gender, imageNumber, fetchCustomers}) {

    const randomUserGender = gender === "MALE" ? "men" : "women";

    const {isOpen, onOpen, onClose} = useDisclosure()
    const cancelRef = useRef();

    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                minW={"250px"}
                w={'full'}
                m={2}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'lg'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                    }
                    objectFit={'cover'}
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/${randomUserGender}/${imageNumber}.jpg`
                        }
                        alt={'Author'}
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={5}>
                    <Stack spacing={1} align={'center'} mb={5}>
                        <Tag borderRadius='full' colorScheme='teal'>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text
                            color={'gray.500'}>Age: {age} | {gender.charAt(0).toUpperCase() + gender.slice(1).toLowerCase()}</Text>
                    </Stack>
                </Box>


                <Stack>

                    <Stack mt={-5} mb={-6} justify={'center'} p={4}>
                        <UpdateCustomerDrawer
                            initialValues={{ name, email, age }}
                            customerId={id}
                            fetchCustomers={fetchCustomers}
                        />
                    </Stack>

                    <Stack m={2} justify={'center'} p={2} >
                        <Button
                            mb={5}
                            bg={"red.400"}
                            rounded={"full"}
                            _hover={{
                                transform: 'translateY(-2px)',
                                boxShadow: 'lg'
                            }}
                            _focus={{
                                bg: 'green.500'
                            }}
                            onClick={onOpen}
                        >
                            Delete
                        </Button>

                        <AlertDialog
                            leastDestructiveRef={cancelRef}
                            isOpen={isOpen}
                            onClose={onClose}
                        >
                            <AlertDialogOverlay>
                                <AlertDialogContent>
                                    <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                                        Delete Customer
                                    </AlertDialogHeader>

                                    <AlertDialogBody>
                                        Are you sure to delete {name}? You can't undo this action afterwards.
                                    </AlertDialogBody>

                                    <AlertDialogFooter>
                                        <Button ref={cancelRef} onClick={onClose}>
                                            Cancel
                                        </Button>
                                        <Button
                                            colorScheme='red'
                                            onClick={() => {
                                                deleteCustomer(id).then(res => {
                                                    console.log(res);
                                                    successNotification(
                                                        "Customer deleted",
                                                        `${name} was successfully deleted`
                                                    );
                                                    fetchCustomers();
                                                }).catch(err => {
                                                    console.log(err);
                                                    errorNotification(
                                                        err.code,
                                                        err.response.data.message
                                                    );
                                                }).finally(() => {
                                                    onClose();
                                                });
                                            }}
                                            ml={3}
                                        >
                                            Delete
                                        </Button>
                                    </AlertDialogFooter>
                                </AlertDialogContent>
                            </AlertDialogOverlay>
                        </AlertDialog>

                    </Stack>

                </Stack>

            </Box>
        </Center>
    );
}
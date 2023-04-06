import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
import {Flex, Heading, Image, Stack, Text} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

const Signup = () => {

    const {customer, setCustomerFromToken} = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'} mb={15}>Register for an account</Heading>
                    <CreateCustomerForm onSuccess={(token) => {
                        localStorage.setItem("access_token", token);
                        setCustomerFromToken();
                        navigate("/dashboard");
                    }} />
                    <h1 align={"center"} >Have and account? <a color={"blue"} href={"/"}>Login now!</a></h1>
                </Stack>
            </Flex>
            <Flex
                flex={1}
                p={10}
                flexDirection={"column"}
                alignItems={"center"}
                justifyContent={"center"}
                style={{background: "linear-gradient(to bottom, #4b6cb7, #182848, #4b6cb7)"}}
            >
                <Text fontSize={"6xl"} color={"white"} fontWeight={"bold"} mb={5}>
                    Full Stack Project
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'cover'}
                    src={
                        'https://user-images.githubusercontent.com/77800578/230351860-11550865-357b-40bf-87a0-c3c907974786.png'
                    }
                />
            </Flex>

        </Stack>
    );
}


export default Signup;
import axios from 'axios'

const instance = axios.create({
    baseURL: 'http://localhost:8080/',
    headers: {
        accept: 'application/json',
        'Access-Control-Allow-Origin': 'http://localhost:8080/catalog'
    }
})

export default instance

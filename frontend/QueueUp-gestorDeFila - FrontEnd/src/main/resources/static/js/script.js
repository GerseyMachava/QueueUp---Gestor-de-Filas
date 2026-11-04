async function getPublicAccessResources(businessSLug, publicCode) {
    try {
        const response = await fetch(
            `http://localhost:8080/api/public/${businessSLug}/queue/${publicCode}`
        )


        if (!response.ok) {
            throw new Error(`HTTP STATUS ${response.status}`)
        }

        const data = await response.json();
        console.log(data);
    } catch (erro) {
        console.error(`Error while trying to fetch `, erro.message)
    }
}

getPublicAccessResources("bim-2", "IcCdfe");



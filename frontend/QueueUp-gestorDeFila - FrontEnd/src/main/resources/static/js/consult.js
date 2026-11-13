import { api } from "./api.js";
let poolingInterval = null;

function init() {
    loadConsult()
}

async function loadConsult() {
    try {
        
        const slug = new URLSearchParams(window.location.search).get('slug') || 'default-slug';
        const publicCode = new URLSearchParams(window.location.search).get('publicCode') || 'default-publicCode';
        const apiResponse = await api.get(`/public/bim-2/queue/consult/IcCdfe`);
        renderTicket(apiResponse.data)
    } catch (error) {
        console.error('Error loading the ticket info.', error);
    }

}

function renderTicket(data) {
    const consultContentElement = document.getElementById("consult-content")
    consultContentElement.textContent =
        `${data.ticketNumber}-${data.custormerName}-${data.status}-${data.categoryName}-${data.estimatedWaitingTime}-${data.calledAt}`

}document.addEventListener("DOMContentLoaded",init)
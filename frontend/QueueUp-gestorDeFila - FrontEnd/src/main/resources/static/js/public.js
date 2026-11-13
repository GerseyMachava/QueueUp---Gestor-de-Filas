import { api } from './api.js'
//Lê slug da URL (?slug=bobs-burgers) ou hardcoded, chama loadCurrent(), inicia polling
let pollingInterval;
function init() {
    loadCurrent();
    //startPolling()
}

//api.get('/public/{slug}/queue/current') (sem token — endpoint público), chama render()
async function loadCurrent() {
    try {
        const slug = new URLSearchParams(window.location.search).get('slug') || 'default-slug';
        const apiResponse = await api.get(`/public/bim-2/queue/current`);
        const ticketData = apiResponse.data;
        console.log(ticketData);
        render(ticketData);
    } catch (error) {
        console.error('Error loading current ticket:', error);
    }
}
//Renderiza current (destaque) e next (lista)
function render(data) {
    renderCurrentCalledTicket(data);
    renderNext(data);

}
//Mostra o último chamado grande
function renderCurrentCalledTicket(ticketData) {
    const currentTicket = ticketData.current;
    const currentTicketElement = document.getElementById("current-ticket");
    currentTicketElement.textContent = currentTicket.ticketNumber + " - " + currentTicket.customerNameMasked + " - " + currentTicket.categoryName;
}

//Lista os próximos 5
function renderNext(ticketData) {
    const waitingTicketsList = ticketData.next;
    const liElement = document.getElementById("waiting-tickets-list")

    liElement.innerHTML = "";


    if (!waitingTicketsList || waitingTicketsList.length === 0) {
        liElement.innerHTML = "<li>Nenhum ticket na lista de espera</li>";

        return
    } return waitingTicketsList.forEach(ticket => {
        const li = document.createElement("li");
        li.textContent = ticket.ticketNumber + " - " + ticket.customerNameMasked + " - " + ticket.status;
        liElement.appendChild(li);
    })
}
function startPolling() {
    pollingInterval = setInterval(loadCurrent, 5000);
}


document.addEventListener("DOMContentLoaded", init);
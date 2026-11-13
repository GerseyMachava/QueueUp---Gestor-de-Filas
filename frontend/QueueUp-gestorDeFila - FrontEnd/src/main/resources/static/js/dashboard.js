import { requireAuth, logout } from './auth.js';
import { api } from './api.js';
let pollingInterval = null;
function init() {
    requireAuth();
    loadQueue();
    handleCallNext();
    handleLogout();
    startPolling();
    renderCurrentCalledTicket();

}


async function loadQueue() {
    try {
        //api.get('/tickets?status=WAITING&sort=priority,asc&sort=createdAt,asc'), chama renderQueue(tickets)
        const apiResponse = await api.get('/tickets')
        const tickets = apiResponse.data.content;
        renderQueue(tickets)



    } catch (error) {
        console.error("Erro ao carregar a fila:", error.message);
        const queueListElement = document.getElementById("queue-list");
        if (queueListElement) {
            queueListElement.innerHTML = "<li style='color:red'>Erro ao carregar os dados da fila.</li>";
        }
    }

}

function renderQueue(tickets) {
    //Limpa #queue-list, se vazio mostra "Nenhum cliente aguardando", senão cria li pra cada ticket
    const liElement = document.getElementById("queue-list");
    liElement.innerHTML = "";


    if (!tickets || tickets.length === 0) {
        liElement.innerHTML = "<li>Nenhum tickt listado</li>";

        return
    }
    return tickets.forEach(ticket => {
        const li = document.createElement("li");
        li.textContent = ticket.customerName + " - " + ticket.ticketNumber + " - " + ticket.priority;
        liElement.appendChild(li);
    })
}
function handleCallNext() {
    //api.post('/queue/next'), chama renderCurrentTicket(ticket), recarrega fila. Em erro (fila vazia), mostra alerta
    const liElement = document.getElementById("call-next-btn");
    liElement.addEventListener("click", async () => {
        try {
            const apiResponse = await api.post('/queue/next');
            const ticket = apiResponse.data;
            renderCalledtTicket(ticket);
            loadQueue();
        } catch (error) {
            console.error(error.message);
            alert("Fila vazia. Nenhum ticket para chamar.");

        }
    })

}


function renderCalledtTicket(ticket) {
    //Mostra o ticket chamado no #current-ticket (destaque grande)
    const currentTicketElement = document.getElementById("current-ticket");
    currentTicketElement.textContent = ticket.customerName + " - " + ticket.ticketNumber + " - " + ticket.priority;
}
async function renderCurrentCalledTicket() {
    try {
        const apiResponse = await api.get('/queue/getCurrentTicket');
        const calledTicket = apiResponse.data;
        renderCalledtTicket(calledTicket);
    } catch (error) {
        console.error(error.message);
        alert("Fila vazia. Nenhum ticket para chamar.");
    }
}

function handleLogout() {
    const logoutButton = document.getElementById("logout-btn");
    logoutButton.addEventListener("click", () => {
        logout();
        window.location.href = "login.html";
    });
}

function startPolling() {
    pollingInterval = setInterval(loadQueue, 5000);
}

function stopPolling() {
    if (pollingInterval) {
        clearInterval(pollingInterval);
        pollingInterval = null;
    }
}
document.addEventListener("DOMContentLoaded", init);
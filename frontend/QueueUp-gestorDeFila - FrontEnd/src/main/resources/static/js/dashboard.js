import { requireAuth, logout } from './auth.js';
import { api } from './api.js';
let pollingInterval = null;
function init() {
    requireAuth();
    loadQueue();
    loadCurrentCalledTicket();
    handleCallNext();
    handleLogout();
    startPolling();
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
    const queueListElement = document.getElementById("queue-list");
    if (!queueListElement) return;
    queueListElement.innerHTML = "";


    if (!tickets || tickets.length === 0) {
        queueListElement.innerHTML = "<li>Nenhum ticket na lista de espera</li>";

        return
    }
    tickets.forEach(ticket => {
        const li = document.createElement("li");
        li.textContent = ticket.customerName + " - " + ticket.ticketNumber + " - " + ticket.priority;
        queueListElement.appendChild(li);
    })
}
function handleCallNext() {
    const callNextBtn = document.getElementById("call-next-btn");
    if (!callNextBtn) return;
    callNextBtn.addEventListener("click", async () => {
        try {
            const apiResponse = await api.post('/queue/next');
            const ticket = apiResponse.data;

            renderCurrentCalledtDisplay(ticket);
            loadQueue();
        } catch (error) {
            console.error("Error ao chamar proximo", error.message);
            alert("fila vazia. Nenhum Ticket para chamar");

        }
    });

}
async function getCurrentCalledTicket() {
    try {
        const apiResponse = await api.get('/queue/getCurrentTicket');
        return apiResponse.data
    } catch (error) {
        console.error("Nenhum ticket chamado no momento", error.message);
        return null
    }
}
async function loadCurrentCalledTicket() {
    const currentTicket = await getCurrentCalledTicket();
    renderCurrentTicketDisplay(currentTicket);
}
function renderCurrentTicketDisplay(ticket) {
    const currentTicketElement = document.getElementById("current-ticket");
    if (!currentTicketElement) return;
    if (!ticket) {
        currentTicketElement.textContent = "Nenhum ticket chamado";
        return;
    }

    currentTicketElement.textContent = `${ticket.customerName} - ${ticket.ticketNumber} - ${ticket.priority}`;
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
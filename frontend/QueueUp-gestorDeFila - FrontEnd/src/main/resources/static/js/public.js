import api from './api.js'
//Lê slug da URL (?slug=bobs-burgers) ou hardcoded, chama loadCurrent(), inicia polling
function init() { }

//api.get('/public/{slug}/queue/current') (sem token — endpoint público), chama render()
function loadCurrent() {
    try {
        const slug = new URLSearchParams(window.location.search).get('slug') || 'default-slug';
        const data = api.get(`/public/${slug}/queue/current`);
        render(data);
    } catch (error) {
        console.error('Error loading current ticket:', error);
    }
}
//Renderiza current (destaque) e next (lista)
function render(data) {

}
//Mostra o último chamado grande
renderCurrent(ticket)

//Lista os próximos 5
renderNext(tickets)
//setInterval(loadCurrent, 5000)
startPolling()
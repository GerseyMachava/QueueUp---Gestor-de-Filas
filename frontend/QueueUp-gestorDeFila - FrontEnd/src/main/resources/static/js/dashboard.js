import { requireAuth } from './auth.js';
import { api } from './api.js';
function init(){
    requireAuth();
    loadQueue();
    const logOutSubmit = document.getElementById('logout');
    const callNextButton = document.getElementById('call-next');

}


function loadQueue(){
   const data = api.get('/tickets')
console.log(data)
}



document.addEventListener("DOMContentLoaded",init);
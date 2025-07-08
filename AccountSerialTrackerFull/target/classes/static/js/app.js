
let stompClient = null;
let initialConnectAttempted = false;


function connect() {
    if (stompClient && stompClient.connected) {
        console.log('Already connected.');
        return;
    }

    const socket = new SockJS('/ws'); 
    stompClient = Stomp.over(socket);
    stompClient.debug = null; 

    if (typeof setConnectionStatus === 'function' && !initialConnectAttempted) {
        setConnectionStatus('connecting', 'Connecting to live server...');
        initialConnectAttempted = true;
    } else if (typeof setConnectionStatus === 'function') { 
        setConnectionStatus('connecting', 'Reconnecting...');
    }


    stompClient.connect({}, function (frame) {
        console.log('Successfully connected to WebSocket: ' + frame);
        if (typeof setConnectionStatus === 'function') {
            setConnectionStatus('connected', 'Live Connection Active');
        }

        stompClient.subscribe('/topic/serials', function (message) {
            try {
                const queueSerials = JSON.parse(message.body);
                updateAllSerialDisplays(queueSerials);
            } catch (e) {
                console.error("Error parsing serials message:", e, message.body);
            }
        });

    }, function(error) {
        console.error('STOMP connection error: ' + error);
        if (typeof setConnectionStatus === 'function') {
            setConnectionStatus('disconnected', 'Disconnected. Will attempt to reconnect.');
        }
        setTimeout(connect, 5000); 
    });
}

/**
 * @param {object} queueSerials - An object like { tuitionFeeSerial: 10, hallFeeSerial: 5, clearanceSerial: 2 }
 */
function updateAllSerialDisplays(queueSerials) {
    console.log('Received serials for update:', queueSerials);

    
    if (typeof updateDisplayAndTimestamp === 'function') {
        updateDisplayAndTimestamp('TUITION_FEE', queueSerials.tuitionFeeSerial);
        updateDisplayAndTimestamp('HALL_FEE', queueSerials.hallFeeSerial);
        updateDisplayAndTimestamp('CLEARANCE', queueSerials.clearanceSerial);
    } else {
        const adminTuition = document.getElementById('tuitionfeeSerialDisplay');
        if (adminTuition) adminTuition.textContent = queueSerials.tuitionFeeSerial;

        const adminHall = document.getElementById('hallfeeSerialDisplay');
        if (adminHall) adminHall.textContent = queueSerials.hallFeeSerial;

        const adminClearance = document.getElementById('clearanceSerialDisplay');
        if (adminClearance) adminClearance.textContent = queueSerials.clearanceSerial;
    }
}



document.addEventListener('DOMContentLoaded', (event) => {

    connect(); 
});

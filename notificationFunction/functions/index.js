'use strict'


const functions = require('firebase-functions');

const admin = require('firebase-admin');

admin.initializeApp();

exports.sendNotification =
functions.database.ref('/notifications/{user_id}/{notification_id}')
    .onWrite((change, context) =>
    {
        const user_id = context.params.user_id;
        const notification= context.params.notification;
        console.log('We have a notification to send to: ', user_id);

        if (!change.after.val()){
            return console.log("A notification has been deleted from db", notification_id);
        }

        const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

        console.log(deviceToken);

        return deviceToken.then(result => {
          const token_id = result.val();

            const payload = {
              notification: {
                title: "Friend Request",
                body: "You've received a new Friend Request",
                icon: "default"
              }
            };

            console.log(token_id);

            return admin.messaging().sendToDevice(token_id, payload).then(response =>{
              console.log("This was the notification feature", response)
              return null;
            });
        });
    });



// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//s
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   response.send("Hello from Firebase!");
//  });

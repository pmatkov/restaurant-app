
document.addEventListener('DOMContentLoaded', function() {

    const form = document.getElementById("form");
    form.addEventListener("submit", function (event) {

        event.preventDefault();
        document.getElementById('message').style.visibility = "visible";

        setTimeout(function() {document.getElementById('message').style.visibility = "hidden";
        }, 1000);

        const data = {
            "name": document.getElementById("name").value,
            "surname": document.getElementById("surname").value,
            "email": document.getElementById("email").value,
            "regular": false,
            "vip": false,
            "reservationType": "web",
            "numberOfGuests": document.getElementById("guests").value,
            "reservationDateTime": document.getElementById("datetime").value
        };

        async function postJSON(data) {
            try {
                const response = await fetch("/reservations", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(data),
                });

                const result = await response.json();
                return result;
            } catch (error) {
                throw error;
            }
        }

        postJSON(data)
            .then(result => {
                console.log("Success:", result);
            })
            .catch(error => {
                console.error("Error:", error);
            });
    });
});

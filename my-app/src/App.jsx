import React, {useEffect, useRef, useState} from "react";
import axios from "axios";
import "./index.css";

const API_URL = "http://localhost:8080/org/flights";

function App() {

    const [flights, setFlights] = useState([]);
    const [formData, setFormData] = useState({
        id: "",
        destination: "",
        departureDateTime: "",
        airport: "",
        availableSeats: 0,
    });

    //initialize WebSocket connection
    const stompClientRef = useRef(null);

    //get all flights from server
    const fetchFlights = async () => {
        try {
            const res = await axios.get(API_URL);
            setFlights(res.data);
        } catch (err) {
            console.error("Failed to fetch flights:", err);
        }
    };

    useEffect(() => {
        fetchFlights();

        const socket = new WebSocket("ws://localhost:8080/ws/flights");

        socket.onmessage = (event) => {
            const message = event.data;
            console.log("WebSocket message:", message);
            fetchFlights();
        };

        return () => {
            socket.close();
        };
    }, []);

    //reset form data la fiecare modificare
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: name === "availableSeats" ? parseInt(value) : value,
        }));
    };

    //submit form data to server (when adding or updating a flight)
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (formData.id) {
                await axios.put(`${API_URL}/${formData.id}`, formData);
            } else {
                await axios.post(API_URL, formData);
            }
            // Reset form after submission
            setFormData({
                id: "",
                destination: "",
                departureDateTime: "",
                airport: "",
                availableSeats: 0,
            });
            fetchFlights();
        } catch (err) {
            console.error("Error saving flight:", err);
        }
    };

    //fill form with flight data for editing
    const handleEdit = (flight) => {
        const isoDate = flight.departureDateTime.replace(" ", "T");
        setFormData({ ...flight, departureDateTime: isoDate });
    };

    //delete flight by id
    const handleDelete = async (id) => {
        try {
            await axios.delete(`${API_URL}/${id}`);
            fetchFlights();
        } catch (err) {
            console.error("Error deleting flight:", err);
        }
    };

    //interfata principala
    return (
        <div className="container">
            <h1>✈️ Flight Manager</h1>

            { /* Form for adding or editing flights */ }
            <form className="flight-form" onSubmit={handleSubmit}>
                <h2>{formData.id ? "Edit Flight" : "Add New Flight"}</h2>

                <label>Destination</label>
                <input type="text" name="destination" value={formData.destination} onChange={handleChange} required />

                <label>Departure Date & Time</label>
                <input type="datetime-local" name="departureDateTime" value={formData.departureDateTime} onChange={handleChange} required />

                <label>Airport</label>
                <input type="text" name="airport" value={formData.airport} onChange={handleChange} required />

                <label>Available Seats</label>
                <input type="number" name="availableSeats" value={formData.availableSeats} onChange={handleChange} required />

                <button className="submit-button" type="submit">
                    {formData.id ? "Update" : "Add"} Flight
                </button>
            </form>

            { /* Table to display all flights */ }
            <h2>All Flights</h2>
            {flights.length === 0 ? (
                <p>No flights found.</p>
            ) : (
                <table className="flights-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Destination</th>
                        <th>Departure</th>
                        <th>Airport</th>
                        <th>Seats</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {flights.map((flight) => (
                        <tr key={flight.id}>
                            <td>{flight.id}</td>
                            <td>{flight.destination}</td>
                            <td>{flight.departureDateTime}</td>
                            <td>{flight.airport}</td>
                            <td>{flight.availableSeats}</td>
                            <td>
                                <button className="edit-button" onClick={() => handleEdit(flight)}>Edit</button>
                                <button className="delete-button" onClick={() => handleDelete(flight.id)}>Delete</button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default App;

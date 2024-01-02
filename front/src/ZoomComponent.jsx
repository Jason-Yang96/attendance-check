import React, { useEffect, useState } from 'react';
import { fetchMeetingDetail, fetchMeetingParticipantDetail } from './api/axiosRequests.js';
import {axiosInstance, clientId, clientSecret} from './api/axiosRequests.js';
import axios from "axios";

const ZoomComponent = () => {

    const [meetingDetail, setMeetingDetail] = useState({});
    const [meetingParticipants, setMeetingParticipants] = useState([]);

    const meetingId = "637 870 7237"; // Replace with the actual meeting ID

    useEffect(() => {
        const fetchData = async () => {
            try {
                const tokenResponse = await axios.post(
                    'https://zoom.us/oauth/token',
                    null,
                    {
                        params: {
                            grant_type: 'authorization_code',
                        },
                        auth: {
                            username: clientId,
                            password: clientSecret,
                        },
                    }
                );
                const accessToken = tokenResponse.data.access_token;
                axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

                const meetingDetailResponse = await fetchMeetingDetail(meetingId);
                setMeetingDetail(meetingDetailResponse.data);

                const meetingParticipantsResponse = await fetchMeetingParticipantDetail(meetingId);
                setMeetingParticipants(meetingParticipantsResponse.data.participants);
            } catch (error) {
                console.error('Fetching error from Zoom API data:', error);
            }
        };

        fetchData();
    }, [meetingId]);

    return (
        <div>
            <h1>Zoom Meeting Detail</h1>
            <div>
                <h2>Meeting Info</h2>
                <pre>{JSON.stringify(meetingDetail, null, 2)}</pre>
            </div>
            <div>
                <h2>Meeting Participants</h2>
                <ul>
                    {meetingParticipants.map((participant) => (
                        <li key={participant.id}>{participant.name}</li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default ZoomComponent;
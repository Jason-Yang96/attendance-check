import axios from 'axios';

export const clientId = "ZbNmeDfgROWOPdtIT649Jg";
export const clientSecret = "sdehsD0wlc9DyQfHT07mfzYfuDbv9VdH";
const accessToken = "";

export const axiosInstance = axios.create({
    baseURL: 'https://api.zoom.us/v2/',
    headers: {
        Authorization: `Bearer ${accessToken}`,
    },
});

const fetchMeetingDetail = (meetingId)=>{
    return axiosInstance.get(`report/meetings/${meetingId}`);
}
const fetchMeetingParticipantDetail = (meetingId) => {
    return axiosInstance.get(`report/meetings/${meetingId}/participants`);
};


export { fetchMeetingDetail, fetchMeetingParticipantDetail };
export default {axiosInstance, clientId, clientSecret};


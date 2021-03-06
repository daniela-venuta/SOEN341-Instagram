import "../../config";
import axios from "axios";
import FollowingButton from "../Following/FollowingButton";
import React, { useState, useEffect } from "react";
import { Link, useHistory } from "react-router-dom";

import "./Profile.scss";

export default function Profile(props) {
	const [errorMsg, setErrorMsg] = useState("");
	const [username, setUsername] = useState("");
	const [biography, setBiography] = useState("");
	const [displayName, setDisplayName] = useState("");
	const [numFollowers, setNumFollowers] = useState(undefined);
	const [numFollowings, setNumFollowings] = useState(undefined);
	const [numPictures, setNumPictures] = useState(undefined);

	const [refreshToggle, setRefreshToggle] = useState(true);
	const [currentClient, setCurrentClient] = useState(undefined);
	const history = useHistory();

	useEffect(() => {
		const loadProfile = () => {
			axios.get(global.config.BACKEND_URL + `/account/profile/${props.username}`)
				.then((response) => {
					setUsername(response.data.username);
					setBiography(response.data.biography);
					setDisplayName(response.data.displayName);
					setNumFollowers(response.data.numFollowers);
					setNumFollowings(response.data.numFollowings);
					setNumPictures(response.data.numPictures);
				})
				.catch((error) => {
					if (error.response && error.response.data && error.response.data.message) {
						setErrorMsg(error.response.data.message);
					}
					else {
						setErrorMsg("An unknown error occurred.");
					}
				})
		}

		const getCurentClient = () => {
			axios.get(global.config.BACKEND_URL + `/account`)
				.then((response) => {
					setCurrentClient(response.data.username);
				})
				.catch(() => {
					alert("Unexpected error, redirecting to home");
					history.push("/");
				});
		}

		loadProfile();
		getCurentClient();

	}, [props, refreshToggle, history])

	const handleFollowChange = () => {
		setRefreshToggle(!refreshToggle);
	}

	return (
		<div className="profile-container">
			<div className="user-details">
				<div className="names">
					<div className="display-name">{displayName}</div>
					<div className="username">({username})</div>
				</div>
				<div className="follow-btn">
					{
						!(props.currentUser === props.username) &&
						<FollowingButton username={props.username} refreshProfile={handleFollowChange} class='following-user' />
					}
					{errorMsg && <div className="error">{errorMsg}</div>}
				</div>
				{
					(currentClient === username) &&
					<div className="edit-btn">
						<Link to={"/accounts/edit"}>
							<div className="gear-icon">
								<svg className="bi bi-gear" width="1.2em" height="1.2em" viewBox="0 0 16 16" fill="currentColor">
										<path fillRule="evenodd" d="M8.837 1.626c-.246-.835-1.428-.835-1.674 0l-.094.319A1.873 1.873 0 014.377 3.06l-.292-.16c-.764-.415-1.6.42-1.184 1.185l.159.292a1.873 1.873 0 01-1.115 2.692l-.319.094c-.835.246-.835 1.428 0 1.674l.319.094a1.873 1.873 0 011.115 2.693l-.16.291c-.415.764.42 1.6 1.185 1.184l.292-.159a1.873 1.873 0 012.692 1.116l.094.318c.246.835 1.428.835 1.674 0l.094-.319a1.873 1.873 0 012.693-1.115l.291.16c.764.415 1.6-.42 1.184-1.185l-.159-.291a1.873 1.873 0 011.116-2.693l.318-.094c.835-.246.835-1.428 0-1.674l-.319-.094a1.873 1.873 0 01-1.115-2.692l.16-.292c.415-.764-.42-1.6-1.185-1.184l-.291.159A1.873 1.873 0 018.93 1.945l-.094-.319zm-2.633-.283c.527-1.79 3.065-1.79 3.592 0l.094.319a.873.873 0 001.255.52l.292-.16c1.64-.892 3.434.901 2.54 2.541l-.159.292a.873.873 0 00.52 1.255l.319.094c1.79.527 1.79 3.065 0 3.592l-.319.094a.873.873 0 00-.52 1.255l.16.292c.893 1.64-.902 3.434-2.541 2.54l-.292-.159a.873.873 0 00-1.255.52l-.094.319c-.527 1.79-3.065 1.79-3.592 0l-.094-.319a.873.873 0 00-1.255-.52l-.292.16c-1.64.893-3.433-.902-2.54-2.541l.159-.292a.873.873 0 00-.52-1.255l-.319-.094c-1.79-.527-1.79-3.065 0-3.592l.319-.094a.873.873 0 00.52-1.255l-.16-.292c-.892-1.64.902-3.433 2.541-2.54l.292.159a.873.873 0 001.255-.52l.094-.319z" clipRule="evenodd" />
										<path fillRule="evenodd" d="M8 5.754a2.246 2.246 0 100 4.492 2.246 2.246 0 000-4.492zM4.754 8a3.246 3.246 0 116.492 0 3.246 3.246 0 01-6.492 0z" clipRule="evenodd" />
								</svg>
							</div>
							<button className="edit-button-component">
								Edit Profile
							</button>
						</Link>
					</div>
				}
			</div>
			<div className="profile-stats">
				<div className="posts">
					{numPictures} posts
				</div>
				<div className="followers">
					{numFollowers} followers
				</div>
				<div className="following">
					{numFollowings} following
				</div>
			</div>
			<div className="profile-bio">
				{biography}
			</div>
		</div>
	)
}
import "../../config";
import axios from "axios";
import React, { useState, useEffect } from "react";

import PostImage from "../Post/PostImage";

import "./User.scss";

export default function User(props) {

    const [errorMsg, setErrorMsg] = useState("");
    const [Pictures, setPictures] = useState([]);
    const [isFollowing, setFollowing] = useState(false);

    const follow = (event) => {
        event.preventDefault();
        axios.post(global.config.BACKEND_URL + "/account/following/newFollower/" + props.username)
            .then(
                (response) => { setFollowing(true); },
                (error) => {
                    if (error.response) { setErrorMsg(error.response.data.message); }
                    else { setErrorMsg("An unknown error occurred"); }
                }
            )
    };

    const unfollow = (event) => {
        event.preventDefault();
        axios.delete(global.config.BACKEND_URL + "/account/following/followerRemoval/" + props.username)
            .then(
                (response) => { setFollowing(false); },
                (error) => {
                    if (error.response) { setErrorMsg(error.response.data.message); }
                    else { setErrorMsg("An unknown error occurred"); }
                }
            )
    };

    useEffect(() => {
        const isUserFollowing = () => {
            axios.get(global.config.BACKEND_URL + "/account/following/" + props.username).then(
                (response) => {
                    setFollowing(response.data);
                }
            ).catch(
                (error) => {
                    if (error.response && error.response.data && error.response.data.message) {
                        setErrorMsg(error.response.data.message);
                    }
                    else {
                        setErrorMsg("An unknown error occurred.");
                    }
                }
            )
        }

        const loadUser = () => {
            axios.get(global.config.BACKEND_URL + "/" + props.username + "/pictures").then(
                (response) => {
                    setErrorMsg("");
                    setPictures(response.data.reverse());
                }
            ).catch(
                (error) => {
                    setPictures([]);
                    if (error.response && error.response.data && error.response.data.message) {
                        setErrorMsg(error.response.data.message);
                    }
                    else {
                        setErrorMsg("An unknown error occurred.");
                    }
                }
            )
        }

        isUserFollowing();
        loadUser();

    }, [props.username, isFollowing])

    return (
        <div className="user-component">
            <form onSubmit={isFollowing ? unfollow : follow}>
                <div className="follow">
                    <button className="follow-button" type="submit">{isFollowing ? "Unfollow" : "Follow"}</button>
                </div>
            </form>
            {errorMsg && <div className="error">{errorMsg}</div>}
            <div className="all-posts">
                {
                    Pictures.map((id) => (
                        <div className="single-post" key={id}>
                            <PostImage pictureId={id} />
                        </div>
                    ))
                }
            </div>
        </div>
    );
};

import React, { useState, useEffect} from "react";

import { getCommentByPicture } from "./CommentAPI";

import Comment from "./Comment";

export default function CommentList(props) {
	const [commentList, setCommentList] = useState([]);
	const [refreshCommentList, setRefreshCommentList] = useState(false);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const loadComments = () => {
			getCommentByPicture(props.postId)
			.then((response) => {
				setCommentList(response);
				setLoading(false);
			});
		}
		loadComments();
	}, [props.postId, props.refreshComment, refreshCommentList]);

	const reloadComment = () => {
		setLoading(true);
		setRefreshCommentList(!refreshCommentList);
	}

	return (
		<div className="comment-list-wrapper">
			<div className="comment-list">
				{
					loading
					? "Loading Comments"
					: commentList.slice().map(comment => <Comment key={comment.id} comment={comment} reloadComment={reloadComment} />)
				}
			</div>
		</div>
	);
}

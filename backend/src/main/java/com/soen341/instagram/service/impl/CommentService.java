package com.soen341.instagram.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Spring Boot
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// Project
import com.soen341.instagram.dao.impl.AccountRepository;
import com.soen341.instagram.dao.impl.CommentRepository;
import com.soen341.instagram.dao.impl.PictureRepository;
import com.soen341.instagram.dto.comment.CommentResponseDTO;
import com.soen341.instagram.exception.comment.CommentLengthTooLongException;
import com.soen341.instagram.exception.comment.CommentNotFoundException;
import com.soen341.instagram.exception.comment.UnauthorizedRightsException;
import com.soen341.instagram.exception.like.MultipleLikeException;
import com.soen341.instagram.exception.like.NoLikeException;
import com.soen341.instagram.exception.picture.InvalidIdException;
import com.soen341.instagram.exception.picture.PictureNotFoundException;
import com.soen341.instagram.model.Account;
import com.soen341.instagram.model.Comment;
import com.soen341.instagram.model.Picture;
import com.soen341.instagram.utils.UserAccessor;

@Service("commentService")
public class CommentService
{
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PictureRepository pictureRepository;
	private final static int MAX_COMMENT_LENGTH = 250;

	public Comment createComment(final String commentContent, final long pictureId)
	{
		if (commentContent.length() > MAX_COMMENT_LENGTH)
		{
			throw new CommentLengthTooLongException("Comment length exceeds " + MAX_COMMENT_LENGTH + " characters");
		}

		final Account account = UserAccessor.getCurrentAccount(accountRepository);
		final Optional<Picture> picture = pictureRepository.findById(pictureId);

		if (!picture.isPresent())
		{
			throw new PictureNotFoundException();
		}

		final Comment comment = new Comment();
		comment.setComment(commentContent);
		comment.setCreated(new Date());
		comment.setAccount(account);
		comment.setPicture(picture.get());
		
		commentRepository.save(comment);

		return comment;
	}

	public void deleteComment(final String commentId)
	{
		final Comment comment = findComment(commentId);
		final String currentUser = UserAccessor.getCurrentAccount(accountRepository).getUsername();
		
		if (comment.getAccount().getUsername().equals(currentUser)
			|| comment.getPicture().getAccount().getUsername().equals(currentUser))
		{
			commentRepository.delete(comment);
		}
		else
		{
			throw new UnauthorizedRightsException();
		}
	}

	public Comment editComment(final String commentId, final String newComment)
	{
		if (newComment.length() > MAX_COMMENT_LENGTH)
		{
			throw new CommentLengthTooLongException("Comment length exceeds " + MAX_COMMENT_LENGTH + " characters");
		}

		final Comment comment = findComment(commentId);

		if (comment.getAccount().getUsername().equals(UserAccessor.getCurrentAccount(accountRepository).getUsername()))
		{
			comment.setComment(newComment);
			commentRepository.save(comment);
		}
		else
		{
			throw new UnauthorizedRightsException();
		}

		return comment;
	}

	public List<Comment> getCommentsByPicture(final long pictureId)
	{
		final Picture picture = findPicture(pictureId);
		return commentRepository.findByPicture(picture);
	}

	private Picture findPicture(final long pictureId)
	{
		Optional<Picture> pictureOptional = pictureRepository.findById(pictureId);
		
		if (!pictureOptional.isPresent())
		{
			throw new InvalidIdException("Picture Id is invalid");
		}
		
		return pictureOptional.get();
	}

	public Comment findComment(final String id)
	{
		long commentId;
		
		try
		{
			commentId = Long.valueOf(id);
		}
		catch (NumberFormatException e)
		{
			throw new InvalidIdException("Invalid comment ID.");
		}
		
		Optional<Comment> commentOptional = commentRepository.findById(commentId);
		
		if (!commentOptional.isPresent())
		{
			throw new CommentNotFoundException();
		}
		
		return commentOptional.get();
	}

	public int likeComment(final String commentId)
	{
		final Comment comment = findComment(commentId);
		final Set<Account> likedBy = comment.getLikedBy();

		if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken))
		{
			final boolean addedSuccessfully = likedBy.add(UserAccessor.getCurrentAccount(accountRepository));
			
			if (!addedSuccessfully)
			{
				throw new MultipleLikeException("You can only like this comment once.");
			}
			
			commentRepository.save(comment);
		}
		
		return comment.getLikeCount();
	}

	public int unlikeComment(final String commentId)
	{
		final Comment comment = findComment(commentId);
		final Set<Account> likedBy = comment.getLikedBy();
		final boolean removedSuccessfully = likedBy.remove(UserAccessor.getCurrentAccount(accountRepository));
		
		if (!removedSuccessfully)
		{
			throw new NoLikeException("You have not liked this comment yet.");
		}
		
		commentRepository.save(comment);
		
		return comment.getLikeCount();
	}

	public boolean getLikeStatus(String commentId)
	{
		if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken))
		{
			final Comment comment = findComment(commentId);
			final Set<Account> likedBy = comment.getLikedBy();
			
			return likedBy.contains(UserAccessor.getCurrentAccount(accountRepository));
		}
		else
		{
			return false;
		}
	}

	public CommentResponseDTO determineEditable(final CommentResponseDTO commentResponseDTO)
	{
		String currentUser = null;
		if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken))
		{
			final Account currentUserRequest = UserAccessor.getCurrentAccount(accountRepository);
			currentUser = currentUserRequest.getUsername();
		}

		final boolean isCurrentUserTheOwnerOfComment =
		(
			currentUser != null
			&& (commentResponseDTO.getAccount().equals(currentUser)
			|| commentResponseDTO.getPictureDTO().getAccount().equals(currentUser))
		);

		commentResponseDTO.setEditable(isCurrentUserTheOwnerOfComment);

		return commentResponseDTO;
	}

}

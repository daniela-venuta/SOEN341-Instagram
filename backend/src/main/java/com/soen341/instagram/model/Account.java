package com.soen341.instagram.model;

// Project
import com.soen341.instagram.exception.account.AlreadyFollowingException;
import com.soen341.instagram.exception.account.SameAccountException;

// Other libraries
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
public class Account
{
	@Id
	private String username;
	@NotNull
	private String email;
	@NotBlank(message = "Password cannot be blank")
	private String password;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private String displayName;
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Account> following;
	private Date dateOfBirth;
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date created;
	private String biography;
	@NotNull
	@ManyToMany
	private Set<Account> followers;
	@OneToOne
	private Picture profilePicture;

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public Date getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getBiography()
	{
		return biography;
	}

	public void setBiography(String biography)
	{
		this.biography = biography;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Picture getProfilePicture()
	{
		return profilePicture;
	}

	public void setProfilePicture(Picture profilePicture)
	{
		this.profilePicture = profilePicture;
	}

	public Set<Account> getFollowing()
	{
		if (following == null)
		{
			following = new HashSet<>();
		}

		return following;
	}

	public Set<Account> getFollowers()
	{
		if (followers == null)
		{
			followers = new HashSet<>();
		}

		return followers;
	}

	public void follow(Account otherAccount)
	{
		if (this.equals(otherAccount))
		{
			throw new SameAccountException("You cannot follow yourself.");
		}
		else if (getFollowing().contains(otherAccount))
		{
			throw new AlreadyFollowingException("You are already following this user.");
		}
		
		getFollowing().add(otherAccount);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		Account account = (Account) o;

		return username.equals(account.username);
	}

	@Override
	public int hashCode()
	{
		return username.hashCode();
	}
}

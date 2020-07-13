# SupportQ
-  SupportQ
## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Inspired by the codepath in-class support queue (hence the name), this app lets students enrolled in a particular course to create a supportive learning environment. Students get to ask, answer and discussion anything course-related to ferment their understaning of the course material. Teachers and TA's of the course will be able to answer students questions or concerns, approve another students response to a particular question, and provide extra resources. 
### App Evaluation
- **Category:** Education 
- **Mobile:** This app would provide mobile first experience and it would work exactly as expected in a website too. 
- **Story:** An app that allows student enrolled in a particular course to post, answer and discuss about course related material.  
- **Market:** Every student enroll in the course will be able to enjoy the full content that the app has to offer. 
- **Habit:** User can make the best of the app by posting any question related to the course at any time. When their concerns are address, they will recieve a push notificaion to check out the response in the app.  
- **Scope:** The app would start out by allowing students registered in the same course only to interact but could potentialy be expanded to allow any course to be in the app so that students will get to have all their courses in the app. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* user can create or log into account
* persistence after a user logs in
* user can post and delete their question/concern
* user can tap on a post and be redirect to a detailed screen
* user can answer/address a question/concern
* user can view a feed of questions/concerns
* user can edit there profile picture by using the camera/gallery to take a picture

**Optional Nice-to-have Stories**

* user gets notified when a question/concern is answered/addressed
* user can see a log of discussion they favorited
* user can pull to refresh home feed
* user gets notified when someone respond to their questions/concerns and when an announcement is made.
* user gets notified to a question/concern they upted to follow when they have responses
* user can tap on a notification and be redirected to a detail screen
* user can turn off notification on the profile screen
* user can search through the feed for a specific question/concern

### 2. Screen Archetypes

* Login screen
   *  user can log in with existing account
* Registration screen
   * user can create a new account
* Stream Screen
   * user can view a feed of questions/concerns and answers
* Details screen
   * user can see the details of the questions/concerns
* FAQ && Notification screen
   * user can see and compose messages to event hosts
* Create Screen
   * user can create a new question/concern 
* Profile screen
   * user can change profile picture, notification settings

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Feed
* Profile
* Create question
* Notification

Optional:
* FAQ

**Flow Navigation** (Screen to Screen)

* Login screen
   * => Home feed
* Registration screen
   * => Home feed
* Create a question/concern 
   * => Returns to home feed after user posts a question/concern
* Stream Screen 
   * => navigation to a detail screen on click of a post
* Profile screen
   * => toggles settings
* Notification screen
   * => navigation to a detail screen on click

## Wireframes
<img src="https://github.com/saikz72/SupportQ/blob/master/assets/wireframe2.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
#### Models

Post

|   property    |      type       |                  description                   |
|:-------------:|:---------------:|:----------------------------------------------:|
|   objectid    |     String      |  unique id for the user post (default field)   |
|    author     | Pointer to User |                  image author                  |
|     image     |      File       |        image that user posts(optional)         |
|  likesCount   |     Number      |          number of likes for the post          |
| commentsCount |     Number      |          number of comments for the post                                      |
|   createdAt   |      Date       |   date when post is created (default field)    |
|approved      |       Boolean   |     post has been approved by TA or Professor |


User
|   property    |      type       |                  description                   |
|:-------------:|:---------------:|:----------------------------------------------:|
|   objectid    |     String      |  unique id for the user (default field)   |
|    username     | String |                  name of the user                 |
|     image     |      File       |        image that user          |
|  email      |       String   |     email address of the user |


Comments
|   property    |      type       |                  description                   |
|:-------------:|:---------------:|:----------------------------------------------:|
|   objectid    |     String      |  unique id for the comment (default field)   |
|    author     | Pointer to User |                  image author                  |
|   createdAt   |      Date       |   date when comment is created (default field)    |
|  updatedAt      |       Date   |     date when comment was updated (default field) |
|approved      |       Boolean   |     post has been approved by TA or Professor |


### Networking
#### List of network request by screen
* Home Feed Screen
    (Read/GET) Query all posts where user is author
    (Create/POST) Create a new like on a post
    (Delete) Delete existing like
    (Create/POST) Create a new comment on a post
    (Delete) Delete existing comment
    
* Create Post Screen
    (Create/POST) Create a new post object
    
* Profile Screen
    (Read/GET) Query logged in user object
    (Update/PUT) Update user profile image
    (Delete) Delete an existing post

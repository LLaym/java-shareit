# :atom: ShareIt
**ShareIt** - sharing service.
## :bulb: How to use
**The main function of the service is to allow users to share belongings.** 
The service enables users to showcase the items they are willing to share, as well as search for the desired item and rent it for a certain period of time.
### User endpoints
- ```POST /users``` - create user
- ```GET /users/{id}``` - get user info
- ```PATCH /users/{id}``` - edit user
- ```DELETE /users/{id}``` - delete user
- ```GET /users``` - show all users
### Item endpoints
- ```POST /items``` - create item
- ```PATCH /items/{id}``` - edit item
- ```GET /items/{id}``` - get item info
- ```GET /items``` - get all items info
- ```GET /items/search?text={text}``` - find item by name or description
- ```POST /items/{itemId}/comment``` - post comment to item
### Booking endpoints
- ```POST /bookings``` - create booking
- ```GET /bookings``` - get all bookings by user
- ```GET /bookings/owner``` - get all bookings by item owner
- ```GET /bookings/{bookingid}}``` - get booking info
- ```PATCH /bookings/{bookingid}}``` - confirm booking status by item owner
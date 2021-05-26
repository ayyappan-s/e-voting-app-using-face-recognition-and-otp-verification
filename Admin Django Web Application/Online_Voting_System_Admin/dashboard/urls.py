from django.urls import path
from .views import *
urlpatterns =[
    path("dashboard",dashboard,name="dashboard"),
    path("availability_checker",availability_checker,name="availability_checker"),
    path("startpoll",startpoll,name="startpoll"),
    path("state_adder",state_adder,name="state_adder"),
    path("deleteCandidate",deleteCandidate,name="deleteCandidate"),
    path("viewactivepoll",viewactivepoll,name="viewactivepoll"),
    path("check_poll_status",check_poll_status,name="check_poll_status"),
    path("endpoll",endpoll,name="endpoll"),
    path("viewendedpoll",viewendedpoll,name="viewendedpoll"),
]

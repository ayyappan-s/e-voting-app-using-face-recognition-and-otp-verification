import datetime
import cv2
import numpy
from django.http import JsonResponse
from PIL import Image

from django.contrib.staticfiles.storage import staticfiles_storage
from django.conf import settings
import pyrebase
import xlrd
from django.views.decorators.csrf import csrf_exempt


# Create your views here.

def dob_validator(request):
    if request.is_ajax():
        dob = request.POST.get('dob', None)
        response = {'msg': "None"}

        if dob is not None:
            date_format = '%d/%m/%Y'
            try:
                date_obj = datetime.datetime.strptime(dob, date_format)
                response['msg'] = 'OK'
            except:
                response['msg'] = 'ERROR'

        return JsonResponse(response)


def face_detector(request):
    if request.is_ajax():

        response = {}
        uploaded_img = request.FILES["image"].read()
        npimg = numpy.fromstring(uploaded_img, numpy.uint8)
        img = cv2.imdecode(npimg, cv2.IMREAD_COLOR)

        face_cascade = cv2.CascadeClassifier(
            staticfiles_storage.path('haarcascades/haarcascade_frontalface_default.xml'))
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray, 1.3, 5)

        print(faces)
        for (x, y, w, h) in faces:
            cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)

        cv2.imwrite("detected.jpg", img)
        count = len(faces)
        print(count)
        if count == 1:
            response['msg'] = 'Image Valid'
        elif count > 1:
            response['msg'] = 'More Than One Face Was Detected.! Invalid Image.'
        elif count == 0:
            response['msg'] = 'No Face Was Detected'
        return JsonResponse(response)


def ajax_voter_add(request):
    if request.is_ajax():
        name = request.POST.get('name')
        phone = request.POST.get('phone')
        dob = request.POST.get('dob')
        district=request.POST.get('district')
        legislative = request.POST.get('legislative_assembly')
        state=request.POST.get("state")
        image = request.FILES['image']
        res = {}
        try:
            firebase = pyrebase.initialize_app(settings.FIREBASE_CONFIG)
            storage = firebase.storage()
            storage.child("images/"+state +"/"+ str(district) + "/"+legislative.replace(" ","")+"/" + phone + ".jpg").put(image)
            image_download_url = storage.child("images/"+state +"/"+ str(district) + "/"+legislative.replace(" ","")+"/" + phone + ".jpg").get_url(None)
            database = firebase.database()
            database.child("voters").child(state).child(str(district)).child(legislative).child(phone).set({
                'name': name,
                'dob': dob,
                'image': image_download_url,
                'voted':0,

            })

            res['msg'] = 'success'
            return JsonResponse(res)
        except Exception as e:
            print(e)
            res['msg'] = 'failure'
            return JsonResponse(res)

@csrf_exempt
def state_submission(request):
        state = request.POST.get("state").replace(" ", "").lower()
        wb = xlrd.open_workbook(staticfiles_storage.path("xls/districts.xlsx"))
        sheet = wb.sheet_by_index(0)
        district = []
        for i in range(sheet.nrows):
            if sheet.cell_value(i, 0).replace(" ", "").lower() == state:
                district.append(sheet.cell_value(i, 1))
        res = {'district': district}
        return JsonResponse(res)

@csrf_exempt
def district_submission(request):
        district = request.POST.get("district").replace(" ","").lower()
        wb = xlrd.open_workbook(staticfiles_storage.path("xls/legislative_list.xlsx"))
        sheet=wb.sheet_by_index(0)
        legislative = []
        for i in range(sheet.nrows):
            if sheet.cell_value(i,0).replace(" ",'').lower()==district:
                legislative.append(sheet.cell_value(i,1))
        res ={'legislative':legislative}
        return JsonResponse(res)

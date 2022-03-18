from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
import json

# Create your views here
def getcars(request):
    if request.method != 'GET':
        return HttpResponse(status=404)

    cursor = connection.cursor()
    cursor.execute('SELECT * FROM cars;')
    rows = cursor.fetchall()

    response = {}
    response['cars'] = rows
    return JsonResponse(response)

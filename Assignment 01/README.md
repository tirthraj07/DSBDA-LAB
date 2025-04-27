### Problem Statement


```text
Perform the following operations using Python on any open-source
dataset (e.g., data.csv)
1. Import all the required Python Libraries.
2. Locate an open-source data from the web (e.g.
https://www.kaggle.com). Provide a clear description of the
data and its source (i.e., URL of the web site).
3. Load the Dataset into pandas’ data frame.
4. Data Preprocessing: check for missing values in the data using
pandas isnull(), describe() function to get some initial statistics.
Provide variable descriptions. Types of variables etc. Check the
dimensions of the data frame.
5. Data Formatting and Data Normalization: Summarize the types
of variables by checking the data types (i.e., character, numeric,
integer, factor, and logical) of the variables in the data set. If
variables are not in the correct data type, apply proper type
conversions.
6. Turn categorical variables into quantitative variables in Python.
In addition to the codes and outputs, explain every operation that you
do in the above steps and explain everything that you do to
import/read/scrape the data set.
```


### Pandas Tutorial


#### 1: Read CSV
```python
import pandas as pd

df = pd.read_csv('data.csv')
print(dt.to_string())
```


Pandas helps in :

- Finding correlation between two or more columns
- What is average value
- Max value
- Min value
Pandas are also able to delete rows that are not relevant, or contains wrong values, like empty or NULL values. This is called cleaning the data.

---

#### 2. Pandas Series

A Pandas Series is like a column in a table.

It is a one-dimensional array holding data of any type. 

Example:

```python
import pandas as pd

a = [1, 7, 2]

myvar = pd.Series(a)

print(myvar)
```

Output:
```
import pandas as pd

a = [1, 7, 2]

myvar = pd.Series(a)

print(myvar)
```

**Lables** : If nothing else is specified, the values are labeled with their index number. With the index argument, you can name your own labels.

Example
```python
import pandas as pd

a = [1, 7, 2]

myvar = pd.Series(a, index = ["x", "y", "z"])

print(myvar)
```

Output:  
```
x    1
y    7
z    2
dtype: int64
```

You can have **Key/Value Objects as Series**
The keys of the dictionary become the labels.

Example

```python
import pandas as pd

calories = {"day1": 420, "day2": 380, "day3": 390}

myvar = pd.Series(calories)

print(myvar)
```

Output:

```
day1    420
day2    380
day3    390
dtype: int64i
```

---

#### 3. Dataframes in Pandas  

Data sets in Pandas are usually multi-dimensional tables, called DataFrames.

Series is like a column, a DataFrame is the whole table.


```python
import pandas as pd

data = {
  "calories": [420, 380, 390],
  "duration": [50, 40, 45]
}

myvar = pd.DataFrame(data)

print(myvar)
```

Output:

```
   calories  duration
0       420        50
1       380        40
2       390        45
```


**Locate Row**

As you can see from the result above, the DataFrame is like a table with rows and columns.

Pandas use the loc attribute to return one or more specified row(s)

```python
print(df.loc[0])
```

Output:

```
  calories    420
  duration     50
  Name: 0, dtype: int64
```

Example 2


```python
print(df.loc[[0, 1]])
```

Output:
```
     calories  duration
  0       420        50
  1       380        40
```


**Indexing Dataframe**  


```python
import pandas as pd

data = {
  "calories": [420, 380, 390],
  "duration": [50, 40, 45]
}

df = pd.DataFrame(data, index = ["day1", "day2", "day3"])

print(df) 

# You can now locate row(s) using the labels

print(df.loc["day2"])

```


Output:

```
        calories  duration
  day1       420        50
  day2       380        40
  day3       390        45


  # Printing day2
  calories    380
  duration     40
  Name: day2, dtype: int64

```


**Load Files Into a DataFrame**

If your data sets are stored in a file, Pandas can load them into a DataFrame.

```python
import pandas as pd

df = pd.read_csv('data.csv')

print(df) 
```

If you have a large DataFrame with many rows, Pandas will only return the first 5 rows, and the last 5 rows

**Reading JSON**

Example

```python
import pandas as pd

df = pd.read_json('data.json')

print(df.to_string()) 
```

Output

```
     Duration  Pulse  Maxpulse  Calories
0          60    110       130     409.1
1          60    117       145     479.0
2          60    103       135     340.0
3          45    109       175     282.4
4          45    117       148     406.0
5          60    102       127     300.5
```

---

#### 4. Data Cleaning

Data cleaning means fixing bad data in your data set.

Bad data could be:

- Empty cells
- Data in wrong format
- Wrong data
- Duplicates

#### 5. Cleaning Empty Cells

Empty cells can potentially give you a wrong result when you analyze data.

**OPTION 1: Remove Rows**

One way to deal with empty cells is to remove rows that contain empty cells.  
This is usually OK, since data sets can be very big, and removing a few rows will not have a big impact on the result.

Example

```python
import pandas as pd

df = pd.read_csv('data.csv')

new_df = df.dropna()

print(new_df.to_string())
```

> By default, the dropna() method returns a new DataFrame, and will **NOT**  change the original.

If you want to change the original DataFrame, use the inplace = True argument:

```python
import pandas as pd

df = pd.read_csv('data.csv')

df.dropna(inplace = True)

print(df.to_string())
```

> Now, the dropna(inplace = True) will NOT return a new DataFrame, but it will remove all rows containing NULL values from the original DataFrame.


**OPTION 2: Replace Empty Values**

Another way of dealing with empty cells is to insert a new value instead.

This way you do not have to delete entire rows just because of some empty cells.

The fillna() method allows us to replace empty cells with a value:

```python
import pandas as pd

df = pd.read_csv('data.csv')

df.fillna(130, inplace = True)
```

Replace Only For Specified Columns
The example above replaces all empty cells in the whole Data Frame.

To only replace empty values for one column, specify the column name for the DataFrame

```python
import pandas as pd

df = pd.read_csv('data.csv')

df["Calories"].fillna(130, inplace = True)
```

> Calculate the MEAN, and replace any empty values with it

#### 6. Mean, Median and Mode

```python
import pandas as pd

df = pd.read_csv('data.csv')

mean = df["Calories"].mean()
median = df["Calories"].median()
mode = df["Calories"].mode()[0]

# Replace by Mean, median or mode. Generally prefer median

df["Calories"].fillna(mean, inplace = True)
```

#### 7. Cleaning Data of Wrong Format

Cells with data of wrong format can make it difficult, or even impossible, to analyze data.

Example

```
     Duration          Date  Pulse  Maxpulse  Calories
...
  23        60  '2020/12/23'    130       101     300.0
  24        45  '2020/12/24'    105       132     246.0
  25        60  '2020/12/25'    102       126     334.5
  26        60      20201226    100       120     250.0
```


```python
import pandas as pd

df = pd.read_csv('data.csv')

df['Date'] = pd.to_datetime(df['Date'])

print(df.to_string())
```

Output

```
     Duration          Date  Pulse  Maxpulse  Calories
...
  23        60  '2020/12/23'    130       101     300.0
  24        45  '2020/12/24'    105       132     246.0
  25        60  '2020/12/25'    102       126     334.5
  26        60  '2020/12/26'    100       120     250.0
```

> If there are empty rows, you still have to remove/update them.

#### 8. Wrong Data

"Wrong data" does not have to be "empty cells" or "wrong format", it can just be wrong, like if someone registered "199" instead of "1.99".

Sometimes you can spot wrong data by looking at the data set, because you have an expectation of what it should be.

```csv
      Duration          Date  Pulse  Maxpulse  Calories
  0         60  '2020/12/01'    110       130     409.1
  1         60  '2020/12/02'    117       145     479.0
  2         60  '2020/12/03'    103       135     340.0
  3         45  '2020/12/04'    109       175     282.4
  4         45  '2020/12/05'    117       148     406.0
  5         60  '2020/12/06'    102       127     300.0
  6         60  '2020/12/07'    110       136     374.0
  7        450  '2020/12/08'    104       134     253.3
```

It doesn't have to be wrong, but taking in consideration that this is the data set of someone's workout sessions, we conclude with the fact that this person did not work out in 450 minutes.

**OPTION 1: Replacing Values**

One way to fix wrong values is to replace them with something else.

Example - Manually

```python

df.loc[7, 'Duration'] = 45
```

Example - Automatically

```python
for x in df.index:
  if df.loc[x, "Duration"] > 120:
    df.loc[x, "Duration"] = 120
```

**OPTION 2: Removing Rows**

```python
for x in df.index:
  if df.loc[x, "Duration"] > 120:
    df.drop(x, inplace = True)
```

#### 8. Discovering Duplicates

Duplicate rows are rows that have been registered more than one time.

```
     Duration          Date  Pulse  Maxpulse  Calories
...
  9         60  '2020/12/10'     98       124     269.0
  10        60  '2020/12/11'    103       147     329.3
  11        60  '2020/12/12'    100       120     250.7
  12        60  '2020/12/12'    100       120     250.7
```

To discover duplicates, we can use the `duplicated()` method.

```python
print(df.duplicated())
```

Output:

```
...
9     False
10    False
11    False
12     True
```

**Removing Duplicates**  
To remove duplicates, use the `drop_duplicates()` method.

Example

```python
df.drop_duplicates(inplace = True)
```

---

#### 9. Data Correlations

The `corr()` method calculates the relationship between each column in your data set.

Example

```python
df.corr()
```

Output:

```
            Duration     Pulse  Maxpulse  Calories
  Duration  1.000000 -0.155408  0.009403  0.922721
  Pulse    -0.155408  1.000000  0.786535  0.025120
  Maxpulse  0.009403  0.786535  1.000000  0.203814
  Calories  0.922721  0.025120  0.203814  1.000000
```

---

#### 10. Pandas Plotting

Refer: https://www.w3schools.com/python/pandas/pandas_plotting.asp

